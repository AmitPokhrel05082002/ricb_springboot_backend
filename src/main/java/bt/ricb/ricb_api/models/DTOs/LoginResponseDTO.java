package bt.ricb.ricb_api.models.DTOs;

public class LoginResponseDTO {

    private String token;
    private String userId;
    private String fullName;
    private String role;
    private String branchId;

    public LoginResponseDTO(String token, String userId, String fullName, String role, String branchId) {
        this.token = token;
        this.userId = userId;
        this.fullName = fullName;
        this.role = role;
        this.branchId = branchId;
    }

    public String getToken() { return token; }
    public String getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public String getBranchId() { return branchId; }
}