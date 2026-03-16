package bt.ricb.ricb_api.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "claim_documents")
public class ClaimDocumentsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="claim_id")
    private Integer claimId;

    @Column(name="zip_file_path")
    private String zipFilePath;

    @Column(name="file_size_kb")
    private Integer fileSizeKb;

    @Column(name="uploaded_at")
    private LocalDateTime uploadedAt;

    @Lob
    @Column(name="file_data", columnDefinition = "BLOB")
    private byte[] fileData;


    public ClaimDocumentsEntity(){}

    public Integer getId(){ return id; }
    public void setId(Integer id){ this.id = id; }

    public Integer getClaimId(){ return claimId; }
    public void setClaimId(Integer claimId){ this.claimId = claimId; }

    public String getZipFilePath(){ return zipFilePath; }
    public void setZipFilePath(String zipFilePath){ this.zipFilePath = zipFilePath; }

    public Integer getFileSizeKb(){ return fileSizeKb; }
    public void setFileSizeKb(Integer fileSizeKb){ this.fileSizeKb = fileSizeKb; }

    public LocalDateTime getUploadedAt(){ return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt){ this.uploadedAt = uploadedAt; }
}