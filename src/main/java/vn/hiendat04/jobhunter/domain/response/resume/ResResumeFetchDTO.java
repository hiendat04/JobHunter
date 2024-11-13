package vn.hiendat04.jobhunter.domain.response.resume;

import java.time.Instant;
import vn.hiendat04.jobhunter.util.constant.ResumeStatusEnum;

public class ResResumeFetchDTO {
    private long id;
    private String email;
    private String url;
    private ResumeStatusEnum status;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    private ResResumeFetchDTO.JobResume job;
    private ResResumeFetchDTO.UserResume user;

    public ResResumeFetchDTO.JobResume getJob() {
        return job;
    }

    public void setJob(ResResumeFetchDTO.JobResume job) {
        this.job = job;
    }

    public ResResumeFetchDTO.UserResume getUser() {
        return user;
    }

    public void setUser(ResResumeFetchDTO.UserResume user) {
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ResumeStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ResumeStatusEnum status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public static class UserResume {
        private long id;
        private String name;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public static class JobResume {
        private long id;
        private String name;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }
}
