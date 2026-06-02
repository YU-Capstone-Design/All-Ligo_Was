package yu.likelion14th.allligo_was.S3.dto;

public enum UploadDomain {
    PROMOTION("promotion"),
    COUPON("coupon"),
    PROFILE("profile"),
    CONTENT("content");

    private final String path;

    UploadDomain(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}