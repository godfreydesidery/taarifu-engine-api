package com.taarifu_engine_api.modules.notification.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service for managing email templates.
 * Provides template processing and variable substitution for emails.
 */
@Service
@Slf4j
public class EmailTemplateService {

    /**
     * Processes an email template with the given variables.
     *
     * @param templateName the name of the template
     * @param variables the variables to substitute in the template
     * @return processed template content
     */
    public String processTemplate(String templateName, Map<String, Object> variables) {
        log.debug("Processing template: {} with variables: {}", templateName, variables);
        
        String template = getTemplateContent(templateName);
        
        if (variables != null) {
            // Process conditional blocks first
            template = processConditionalBlocks(template, variables);
            
            // Then process simple variable substitution
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                String placeholder = "{{" + entry.getKey() + "}}";
                String value = entry.getValue() != null ? entry.getValue().toString() : "";
                template = template.replace(placeholder, value);
            }
        }
        
        return template;
    }

    /**
     * Processes conditional blocks in templates (e.g., {{#condition}}...{{/condition}}).
     *
     * @param template the template content
     * @param variables the variables to check conditions against
     * @return template with conditional blocks processed
     */
    private String processConditionalBlocks(String template, Map<String, Object> variables) {
        // Process positive conditions {{#variable}}...{{/variable}}
        String positivePattern = "\\{\\{#(\\w+)\\}\\}(.*?)\\{\\{/\\1\\}\\}";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(positivePattern, java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher matcher = pattern.matcher(template);
        
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String variableName = matcher.group(1);
            String blockContent = matcher.group(2);
            
            Object variableValue = variables.get(variableName);
            boolean conditionMet = false;
            
            if (variableValue instanceof Boolean) {
                conditionMet = (Boolean) variableValue;
            } else if (variableValue != null) {
                conditionMet = true;
            }
            
            if (conditionMet) {
                matcher.appendReplacement(result, blockContent);
            } else {
                matcher.appendReplacement(result, "");
            }
        }
        matcher.appendTail(result);
        template = result.toString();
        
        // Process negative conditions {{^variable}}...{{/variable}}
        String negativePattern = "\\{\\{\\^(\\w+)\\}\\}(.*?)\\{\\{/\\1\\}\\}";
        pattern = java.util.regex.Pattern.compile(negativePattern, java.util.regex.Pattern.DOTALL);
        matcher = pattern.matcher(template);
        
        result = new StringBuffer();
        while (matcher.find()) {
            String variableName = matcher.group(1);
            String blockContent = matcher.group(2);
            
            Object variableValue = variables.get(variableName);
            boolean conditionMet = false;
            
            if (variableValue instanceof Boolean) {
                conditionMet = (Boolean) variableValue;
            } else if (variableValue != null) {
                conditionMet = true;
            }
            
            if (!conditionMet) {
                matcher.appendReplacement(result, blockContent);
            } else {
                matcher.appendReplacement(result, "");
            }
        }
        matcher.appendTail(result);
        
        return result.toString();
    }

    /**
     * Gets the content of a template by name.
     * In production, this would load templates from files, database, or external service.
     *
     * @param templateName the name of the template
     * @return template content
     */
    private String getTemplateContent(String templateName) {
        return switch (templateName) {
            case "admin_user_created" -> getAdminUserCreatedTemplate();
            case "admin_user_updated" -> getAdminUserUpdatedTemplate();
            case "admin_user_activated" -> getAdminUserActivatedTemplate();
            case "admin_user_deactivated" -> getAdminUserDeactivatedTemplate();
            case "admin_user_suspended" -> getAdminUserSuspendedTemplate();
            case "password_reset" -> getPasswordResetTemplate();
            case "password_changed" -> getPasswordChangedTemplate();
            case "login_alert" -> getLoginAlertTemplate();
            case "welcome" -> getWelcomeTemplate();
            case "system_maintenance" -> getSystemMaintenanceTemplate();
            case "system_alert" -> getSystemAlertTemplate();
            default -> getDefaultTemplate();
        };
    }

    /**
     * Admin user created email template.
     */
    private String getAdminUserCreatedTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Admin User Created</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #f8f9fa; padding: 20px; text-align: center; border-radius: 5px; }
                    .content { padding: 20px; }
                    .footer { background-color: #f8f9fa; padding: 20px; text-align: center; border-radius: 5px; margin-top: 20px; }
                    .button { display: inline-block; padding: 10px 20px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px; }
                    .password-box { background-color: #fff3cd; border: 2px solid #ffeaa7; padding: 15px; border-radius: 5px; margin: 15px 0; text-align: center; }
                    .password { font-family: 'Courier New', monospace; font-size: 18px; font-weight: bold; color: #856404; letter-spacing: 2px; }
                    .security-notice { background-color: #f8d7da; border: 1px solid #f5c6cb; padding: 15px; border-radius: 5px; margin: 15px 0; }
                    .important { color: #dc3545; font-weight: bold; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Admin Account Created</h1>
                    </div>
                    <div class="content">
                        <p>Hello <strong>{{username}}</strong>,</p>
                        <p>Your admin account has been successfully created in the Taarifu Engine system.</p>
                        
                        <p><strong>Account Details:</strong></p>
                        <ul>
                            <li>Username: {{username}}</li>
                            <li>Email: {{email}}</li>
                            <li>Account Type: Administrator</li>
                        </ul>
                        
                        {{#passwordGenerated}}
                        <div class="password-box">
                            <p><strong>Your Temporary Password:</strong></p>
                            <div class="password">{{password}}</div>
                            <p><small>This password was automatically generated for security.</small></p>
                        </div>
                        
                        <div class="security-notice">
                            <p class="important">⚠️ SECURITY NOTICE:</p>
                            <ul>
                                <li>Please log in immediately and change this password</li>
                                <li>Do not share this password with anyone</li>
                                <li>Use a strong, unique password for your account</li>
                                <li>This temporary password will expire if not changed</li>
                            </ul>
                        </div>
                        {{/passwordGenerated}}
                        
                        {{^passwordGenerated}}
                        <p>Please log in using your provided password and change it immediately for security purposes.</p>
                        {{/passwordGenerated}}
                        
                        {{#requirePasswordChange}}
                        <p><strong>Important:</strong> You will be required to change your password on your first login.</p>
                        {{/requirePasswordChange}}
                        
                        <p>If you have any questions or need assistance, please contact the system administrator.</p>
                    </div>
                    <div class="footer">
                        <p>Best regards,<br><strong>Taarifu Team</strong></p>
                        <p><small>This is an automated message. Please do not reply to this email.</small></p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }

    /**
     * Admin user updated email template.
     */
    private String getAdminUserUpdatedTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Admin User Updated</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #f8f9fa; padding: 20px; text-align: center; border-radius: 5px; }
                    .content { padding: 20px; }
                    .footer { background-color: #f8f9fa; padding: 20px; text-align: center; border-radius: 5px; margin-top: 20px; }
                    .alert { background-color: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 5px; margin: 15px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Admin Account Updated</h1>
                    </div>
                    <div class="content">
                        <p>Hello <strong>{{username}}</strong>,</p>
                        <p>Your admin account has been updated in the Taarifu Engine system.</p>
                        <div class="alert">
                            <p><strong>Security Notice:</strong> If you did not make these changes, please contact the system administrator immediately.</p>
                        </div>
                        <p>Changes may include:</p>
                        <ul>
                            <li>Profile information updates</li>
                            <li>Password changes</li>
                            <li>Account settings modifications</li>
                        </ul>
                    </div>
                    <div class="footer">
                        <p>Best regards,<br><strong>Taarifu Team</strong></p>
                        <p><small>This is an automated message. Please do not reply to this email.</small></p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }

    /**
     * Admin user activated email template.
     */
    private String getAdminUserActivatedTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Admin User Activated</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #d4edda; padding: 20px; text-align: center; border-radius: 5px; }
                    .content { padding: 20px; }
                    .footer { background-color: #f8f9fa; padding: 20px; text-align: center; border-radius: 5px; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Account Activated</h1>
                    </div>
                    <div class="content">
                        <p>Hello <strong>{{username}}</strong>,</p>
                        <p>Your admin account has been activated and you can now access the Taarifu Engine system.</p>
                        <p>You can log in using your credentials and start using the system.</p>
                    </div>
                    <div class="footer">
                        <p>Best regards,<br><strong>Taarifu Team</strong></p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }

    /**
     * Admin user deactivated email template.
     */
    private String getAdminUserDeactivatedTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Admin User Deactivated</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #f8d7da; padding: 20px; text-align: center; border-radius: 5px; }
                    .content { padding: 20px; }
                    .footer { background-color: #f8f9fa; padding: 20px; text-align: center; border-radius: 5px; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Account Deactivated</h1>
                    </div>
                    <div class="content">
                        <p>Hello <strong>{{username}}</strong>,</p>
                        <p>Your admin account has been deactivated in the Taarifu Engine system.</p>
                        <p>You will no longer be able to access the system until your account is reactivated by an administrator.</p>
                        <p>If you believe this is an error, please contact the system administrator.</p>
                    </div>
                    <div class="footer">
                        <p>Best regards,<br><strong>Taarifu Team</strong></p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }

    /**
     * Admin user suspended email template.
     */
    private String getAdminUserSuspendedTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Admin User Suspended</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #f8d7da; padding: 20px; text-align: center; border-radius: 5px; }
                    .content { padding: 20px; }
                    .footer { background-color: #f8f9fa; padding: 20px; text-align: center; border-radius: 5px; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Account Suspended</h1>
                    </div>
                    <div class="content">
                        <p>Hello <strong>{{username}}</strong>,</p>
                        <p>Your admin account has been suspended in the Taarifu Engine system.</p>
                        <p>Your account access has been temporarily restricted. Please contact the system administrator for more information.</p>
                    </div>
                    <div class="footer">
                        <p>Best regards,<br><strong>Taarifu Team</strong></p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }

    /**
     * Password reset email template.
     */
    private String getPasswordResetTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Password Reset</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #f8f9fa; padding: 20px; text-align: center; border-radius: 5px; }
                    .content { padding: 20px; }
                    .footer { background-color: #f8f9fa; padding: 20px; text-align: center; border-radius: 5px; margin-top: 20px; }
                    .button { display: inline-block; padding: 10px 20px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Password Reset Request</h1>
                    </div>
                    <div class="content">
                        <p>Hello <strong>{{username}}</strong>,</p>
                        <p>You have requested to reset your password for your Taarifu Engine account.</p>
                        <p>Click the button below to reset your password:</p>
                        <p><a href="{{resetLink}}" class="button">Reset Password</a></p>
                        <p>This link will expire in {{expirationTime}} hours.</p>
                        <p>If you did not request this password reset, please ignore this email.</p>
                    </div>
                    <div class="footer">
                        <p>Best regards,<br><strong>Taarifu Team</strong></p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }

    /**
     * Password changed email template.
     */
    private String getPasswordChangedTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Password Changed</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #d4edda; padding: 20px; text-align: center; border-radius: 5px; }
                    .content { padding: 20px; }
                    .footer { background-color: #f8f9fa; padding: 20px; text-align: center; border-radius: 5px; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Password Changed Successfully</h1>
                    </div>
                    <div class="content">
                        <p>Hello <strong>{{username}}</strong>,</p>
                        <p>Your password has been successfully changed for your Taarifu Engine account.</p>
                        <p>If you did not make this change, please contact the system administrator immediately.</p>
                    </div>
                    <div class="footer">
                        <p>Best regards,<br><strong>Taarifu Team</strong></p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }

    /**
     * Login alert email template.
     */
    private String getLoginAlertTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Login Alert</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #fff3cd; padding: 20px; text-align: center; border-radius: 5px; }
                    .content { padding: 20px; }
                    .footer { background-color: #f8f9fa; padding: 20px; text-align: center; border-radius: 5px; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Login Alert</h1>
                    </div>
                    <div class="content">
                        <p>Hello <strong>{{username}}</strong>,</p>
                        <p>We detected a new login to your Taarifu Engine account.</p>
                        <p><strong>Login Details:</strong></p>
                        <ul>
                            <li>Time: {{loginTime}}</li>
                            <li>IP Address: {{ipAddress}}</li>
                            <li>Location: {{location}}</li>
                        </ul>
                        <p>If this was not you, please change your password immediately and contact support.</p>
                    </div>
                    <div class="footer">
                        <p>Best regards,<br><strong>Taarifu Team</strong></p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }

    /**
     * Welcome email template.
     */
    private String getWelcomeTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Welcome</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #d4edda; padding: 20px; text-align: center; border-radius: 5px; }
                    .content { padding: 20px; }
                    .footer { background-color: #f8f9fa; padding: 20px; text-align: center; border-radius: 5px; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Welcome to Taarifu Engine</h1>
                    </div>
                    <div class="content">
                        <p>Hello <strong>{{username}}</strong>,</p>
                        <p>Welcome to the Taarifu Engine system!</p>
                        <p>We're excited to have you on board. You can now access all the features and start using the system.</p>
                    </div>
                    <div class="footer">
                        <p>Best regards,<br><strong>Taarifu Team</strong></p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }

    /**
     * System maintenance email template.
     */
    private String getSystemMaintenanceTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>System Maintenance</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #fff3cd; padding: 20px; text-align: center; border-radius: 5px; }
                    .content { padding: 20px; }
                    .footer { background-color: #f8f9fa; padding: 20px; text-align: center; border-radius: 5px; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>System Maintenance Notice</h1>
                    </div>
                    <div class="content">
                        <p>Hello <strong>{{username}}</strong>,</p>
                        <p>The Taarifu Engine system will undergo scheduled maintenance.</p>
                        <p><strong>Maintenance Details:</strong></p>
                        <ul>
                            <li>Start Time: {{startTime}}</li>
                            <li>End Time: {{endTime}}</li>
                            <li>Duration: {{duration}}</li>
                        </ul>
                        <p>During this time, the system may be temporarily unavailable. We apologize for any inconvenience.</p>
                    </div>
                    <div class="footer">
                        <p>Best regards,<br><strong>Taarifu Team</strong></p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }

    /**
     * System alert email template.
     */
    private String getSystemAlertTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>System Alert</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #f8d7da; padding: 20px; text-align: center; border-radius: 5px; }
                    .content { padding: 20px; }
                    .footer { background-color: #f8f9fa; padding: 20px; text-align: center; border-radius: 5px; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>System Alert</h1>
                    </div>
                    <div class="content">
                        <p>Hello <strong>{{username}}</strong>,</p>
                        <p><strong>Alert Level:</strong> {{alertLevel}}</p>
                        <p><strong>Message:</strong> {{message}}</p>
                        <p><strong>Time:</strong> {{alertTime}}</p>
                        <p>Please take appropriate action as needed.</p>
                    </div>
                    <div class="footer">
                        <p>Best regards,<br><strong>Taarifu Team</strong></p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }

    /**
     * Default email template.
     */
    private String getDefaultTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Notification</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #f8f9fa; padding: 20px; text-align: center; border-radius: 5px; }
                    .content { padding: 20px; }
                    .footer { background-color: #f8f9fa; padding: 20px; text-align: center; border-radius: 5px; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Notification</h1>
                    </div>
                    <div class="content">
                        <p>{{message}}</p>
                    </div>
                    <div class="footer">
                        <p>Best regards,<br><strong>Taarifu Team</strong></p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
}
