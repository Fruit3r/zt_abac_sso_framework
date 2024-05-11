package sso.models.dtos;

public class ResetPasswordDTO {

    String newPassword;
    String passwordResetCode;

    public ResetPasswordDTO(String newPassword, String passwordResetCode) {
        this.newPassword = newPassword;
        this.passwordResetCode = passwordResetCode;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getPasswordResetCode() {
        return passwordResetCode;
    }

    public void setPasswordResetCode(String passwordResetCode) {
        this.passwordResetCode = passwordResetCode;
    }
}
