package TestUtils;

import java.io.IOException;
import java.time.Duration;

import com.mailosaur.MailosaurClient;
import com.mailosaur.MailosaurException;
import com.mailosaur.models.Link;
import com.mailosaur.models.Message;
import com.mailosaur.models.MessageListResult;
import com.mailosaur.models.MessageSearchParams;
import com.mailosaur.models.MessageSummary;
import com.mailosaur.models.SearchCriteria;

import utils.EmailConfig;

public class EmailService {

	private MailosaurClient mailosaur;
	private String serverId;

	public EmailService() {
		this.mailosaur = new MailosaurClient(EmailConfig.MAILOSAUR_API_KEY);
		this.serverId = EmailConfig.MAILOSAUR_SERVER_ID;
	}

	/**
	 * Generate unique email address for testing
	 */
	public String generateTestEmail() {
		return "test" + System.currentTimeMillis() + "@" + EmailConfig.MAILOSAUR_SERVER_DOMAIN;
	}

	/**
	 * Generate email with custom prefix
	 */
	public String generateTestEmail(String prefix) {
		return prefix + "." + System.currentTimeMillis() + "@" + EmailConfig.MAILOSAUR_SERVER_DOMAIN;
	}
//
//	/**
//	 * Wait for email to specific address
//	 */
//	public Message waitForEmail(String emailAddress) throws MailosaurException, IOException {
//		return retryEmailOperation(() -> {
//			MessageSearchParams params = new MessageSearchParams();
//			params.withServer(serverId);
//
//			SearchCriteria criteria = new SearchCriteria();
//			criteria.withSentTo(emailAddress);
//
//			return mailosaur.messages().get(params, criteria, Duration.ofSeconds(EmailConfig.EMAIL_TIMEOUT));
//		});
//	}
	
    /**
     * Wait for email to specific address - SIMPLIFIED VERSION
     */
    @SuppressWarnings("deprecation")
	public Message waitForEmail(String emailAddress) throws MailosaurException, IOException {
        SearchCriteria criteria = new SearchCriteria();
        criteria.withSentTo(emailAddress);
        
        return mailosaur.messages().get(serverId, criteria, EmailConfig.EMAIL_TIMEOUT);
    }
    
//    /**
//     * Alternative search method - looks at recent emails first
//     */
//    private Message searchRecentEmails(String emailAddress) throws MailosaurException, IOException {
//        MessageSearchParams params = new MessageSearchParams();
//        params.withServer(serverId);
//        
//        SearchCriteria criteria = new SearchCriteria();
//        criteria.withSentTo(emailAddress);
//        
//        // Search for messages in the last hour
//        MessageListResult result = mailosaur.messages().search(params, criteria);
//        
//        if (result.items().isEmpty()) {
//            throw new RuntimeException("No email found for address: " + emailAddress + 
//                                     ". Check if email was sent to correct address.");
//        }
//        
//        Message message = result.items().get(0); // Get most recent
//        System.out.println("✅ Found email via alternative search! Subject: " + message.subject());
//        return message;
//    }

    
    /**
     * Wait for email with specific subject - SIMPLIFIED VERSION
     */
    @SuppressWarnings("deprecation")
	public Message waitForEmailWithSubject(String subject) throws MailosaurException, IOException {
        SearchCriteria criteria = new SearchCriteria();
        criteria.withSubject(subject);
        
        return mailosaur.messages().get(serverId, criteria, EmailConfig.EMAIL_TIMEOUT);
    }
    
    /**
     * Wait for email with retry logic - ENHANCED VERSION
     */
    @SuppressWarnings("deprecation")
	public Message waitForEmailWithRetry(String emailAddress) throws MailosaurException, IOException {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= EmailConfig.EMAIL_RETRY_ATTEMPTS; attempt++) {
            try {
                SearchCriteria criteria = new SearchCriteria();
                criteria.withSentTo(emailAddress);
                
                return mailosaur.messages().get(serverId, criteria, EmailConfig.EMAIL_TIMEOUT);
                
            } catch (Exception e) {
                lastException = e;
                System.out.println("Email retrieval attempt " + attempt + " failed: " + e.getMessage());
                
                if (attempt < EmailConfig.EMAIL_RETRY_ATTEMPTS) {
                    try {
                        Thread.sleep(EmailConfig.EMAIL_RETRY_DELAY * 1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted during email retry", ie);
                    }
                }
            }
        }
        
        // Re-throw the last exception
        if (lastException instanceof MailosaurException) {
            throw (MailosaurException) lastException;
        } else if (lastException instanceof IOException) {
            throw (IOException) lastException;
        }
        throw new RuntimeException("Failed to get email after " + EmailConfig.EMAIL_RETRY_ATTEMPTS + " attempts", lastException);
    }
    
    /**
     * Wait for email with retry using NEW API
     */
    public MessageSummary waitForEmailWithRetryNew(String emailAddress) throws MailosaurException, IOException {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= EmailConfig.EMAIL_RETRY_ATTEMPTS; attempt++) {
            try {
                SearchCriteria criteria = new SearchCriteria();
                criteria.withSentTo(emailAddress);
                
                MessageSearchParams params = new MessageSearchParams();
                params.withServer(serverId);
                params.withTimeout(EmailConfig.EMAIL_TIMEOUT * 1000);
                
                MessageListResult result = mailosaur.messages().search(params, criteria);
                
                if (!result.items().isEmpty()) {
                    return result.items().get(0);
                } else {
                    throw new RuntimeException("No email found for: " + emailAddress);
                }
                
            } catch (Exception e) {
                lastException = e;
                System.out.println("Email retrieval attempt " + attempt + " failed: " + e.getMessage());
                
                if (attempt < EmailConfig.EMAIL_RETRY_ATTEMPTS) {
                    try {
                        Thread.sleep(EmailConfig.EMAIL_RETRY_DELAY * 1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted during email retry", ie);
                    }
                }
            }
        }
        
        if (lastException instanceof MailosaurException) {
            throw (MailosaurException) lastException;
        } else if (lastException instanceof IOException) {
            throw (IOException) lastException;
        }
        throw new RuntimeException("Failed to get email after " + EmailConfig.EMAIL_RETRY_ATTEMPTS + " attempts", lastException);
    }



	/**
	 * Extract verification code from email (supports multiple patterns)
	 */
	public String getVerificationCode(Message message) {
		String emailContent = message.text().body();

		// Common verification code patterns
		String[] patterns = { "\\b\\d{6}\\b", // 6 digits
				"\\b\\d{4}\\b", // 4 digits
				"(?i)code[:\\s]*([A-Z0-9]{4,8})", // "Code: ABC123"
				"(?i)verification[:\\s]*([A-Z0-9]{4,8})", // "Verification: 123456"
				"(?i)pin[:\\s]*([A-Z0-9]{4,8})" // "PIN: 1234"
		};

		for (String pattern : patterns) {
			java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
			java.util.regex.Matcher m = p.matcher(emailContent);
			if (m.find()) {
				return m.groupCount() > 0 ? m.group(1).trim() : m.group(0).trim();
			}
		}
		return null;
	}

	/**
	 * Get first link from email
	 */
	public String getFirstLink(Message message) {
		if (message.html() != null && message.html().links() != null && !message.html().links().isEmpty()) {
			return message.html().links().get(0).href();
		}
		return null;
	}

	/**
	 * Get reset password link
	 */
	public String getResetPasswordLink(Message message) {
		if (message.html() != null && message.html().links() != null) {
			for (Link link : message.html().links()) {
				String href = link.href().toLowerCase();
				if (href.contains("reset") || href.contains("password") || href.contains("recover")) {
					return link.href();
				}
			}
		}
		return null;
	}

	/**
	 * Validate email contains expected content
	 */
	public boolean validateEmailContent(Message message, String... expectedContent) {
		String emailBody = message.text().body().toLowerCase();
		for (String content : expectedContent) {
			if (!emailBody.contains(content.toLowerCase())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Retry logic for email operations
	 */
	@SuppressWarnings("unused")
	private Message retryEmailOperation(EmailOperation operation) throws MailosaurException, IOException {
		Exception lastException = null;

		for (int attempt = 1; attempt <= EmailConfig.EMAIL_RETRY_ATTEMPTS; attempt++) {
			try {
				return operation.execute();
			} catch (Exception e) {
				lastException = e;
				if (attempt < EmailConfig.EMAIL_RETRY_ATTEMPTS) {
					try {
						Thread.sleep(EmailConfig.EMAIL_RETRY_DELAY * 1000);
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
						throw new RuntimeException("Interrupted during email retry", ie);
					}
				}
			}
		}

		// Re-throw the last exception
		if (lastException instanceof MailosaurException) {
			throw (MailosaurException) lastException;
		} else if (lastException instanceof IOException) {
			throw (IOException) lastException;
		}
		throw new RuntimeException("Failed after " + EmailConfig.EMAIL_RETRY_ATTEMPTS + " attempts", lastException);
	}

	/**
	 * Clean up all emails
	 */
	public void cleanupEmails() throws MailosaurException, IOException {
		mailosaur.messages().deleteAll(serverId);
	}
}

// Functional interface for email operations
@FunctionalInterface
interface EmailOperation {
	Message execute() throws MailosaurException, IOException;

}
