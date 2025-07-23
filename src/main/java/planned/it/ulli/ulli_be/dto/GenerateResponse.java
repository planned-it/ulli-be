package planned.it.ulli.ulli_be.dto;

public class GenerateResponse {
    private String imageUrl;
    private String provider;
    private String prompt;

    public GenerateResponse() {}

    public GenerateResponse(String imageUrl, String provider, String prompt) {
        this.imageUrl = imageUrl;
        this.provider = provider;
        this.prompt = prompt;
    }

    // Getters and Setters
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
}
