package vn.hiendat04.jobhunter.domain.response.resume;

import java.time.Instant;

public class ResResumeUpdateDTO {
    private Instant updatedAt;
    private String updatedBy;

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

}