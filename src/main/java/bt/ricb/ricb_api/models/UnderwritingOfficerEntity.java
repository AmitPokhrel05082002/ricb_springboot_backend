package bt.ricb.ricb_api.models;

import jakarta.persistence.*;

@Entity
@Table(name = "underwriting_officers")
public class UnderwritingOfficerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String empName;

    private String empId;

    private String branchCode;

    private String email;

    private String mobileNo;


    public UnderwritingOfficerEntity() {
    }


    public UnderwritingOfficerEntity(String empName, String empId, String branchCode,
                                     String email, String mobileNo) {
        this.empName = empName;
        this.empId = empId;
        this.branchCode = branchCode;
        this.email = email;
        this.mobileNo = mobileNo;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }


    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }


    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }
}