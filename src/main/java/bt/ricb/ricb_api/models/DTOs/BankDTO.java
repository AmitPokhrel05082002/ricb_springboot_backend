package bt.ricb.ricb_api.models.DTOs;

import java.sql.Timestamp;

public class BankDTO {

    private Integer id;
    private String name;
    private Timestamp createdAt;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}