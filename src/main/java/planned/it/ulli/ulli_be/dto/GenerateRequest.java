package planned.it.ulli.ulli_be.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class GenerateRequest {

    @NotBlank(message = "텍스트는 필수입니다.")
    private String text;

    @NotNull
    private List<String> styles;

    @NotNull
    private List<String> customStyles;

    @NotNull
    private List<String> customColors;

    private String customPrompt;

    private boolean isRegenerate;

    // Constructors
    public GenerateRequest() {}

    public GenerateRequest(String text, List<String> styles, List<String> customStyles,
                           List<String> customColors, String customPrompt, boolean isRegenerate) {
        this.text = text;
        this.styles = styles;
        this.customStyles = customStyles;
        this.customColors = customColors;
        this.customPrompt = customPrompt;
        this.isRegenerate = isRegenerate;
    }

    // Getters and Setters
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public List<String> getStyles() { return styles; }
    public void setStyles(List<String> styles) { this.styles = styles; }

    public List<String> getCustomStyles() { return customStyles; }
    public void setCustomStyles(List<String> customStyles) { this.customStyles = customStyles; }

    public List<String> getCustomColors() { return customColors; }
    public void setCustomColors(List<String> customColors) { this.customColors = customColors; }

    public String getCustomPrompt() { return customPrompt; }
    public void setCustomPrompt(String customPrompt) { this.customPrompt = customPrompt; }

    public boolean isRegenerate() { return isRegenerate; }
    public void setRegenerate(boolean regenerate) { isRegenerate = regenerate; }
}
