package planned.it.ulli.ulli_be.dto;

import lombok.Data;

@Data
public class DesignRequest {
    private String text;
    private String[] styles;
    private String[] customColors;
    private String customPrompt;
}
