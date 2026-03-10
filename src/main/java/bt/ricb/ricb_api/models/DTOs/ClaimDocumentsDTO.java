package bt.ricb.ricb_api.models.DTOs;

public class ClaimDocumentsDTO {

    private String zipFilePath;
    private Integer fileSizeKb;

    public ClaimDocumentsDTO() {}

    // Getter and Setter for zipFilePath
    public String getZipFilePath() {
        return zipFilePath;
    }

    public void setZipFilePath(String zipFilePath) {
        this.zipFilePath = zipFilePath;
    }

    // Getter and Setter for fileSizeKb
    public Integer getFileSizeKb() {
        return fileSizeKb;
    }

    public void setFileSizeKb(Integer fileSizeKb) {
        this.fileSizeKb = fileSizeKb;
    }
}