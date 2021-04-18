package wrapper;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "date",
        "post_id",
        "text"
})
public class Comment implements Serializable
{

    @JsonProperty("date")
    private String date;
    @JsonProperty("post_id")
    private Integer postId;
    @JsonProperty("text")
    private String text = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = 6484356920977011390L;

    public Comment() {
    }

    /**
     *
     * @param date
     * @param postId
     * @param text
     */
    public Comment(String date, Integer postId, String text) {
        super();
        this.date = date;
        this.postId = postId;
        this.text = text;
    }

    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(String date) {
        this.date = date;
    }

    @JsonProperty("post_id")
    public Integer getPostId() {
        return postId;
    }

    @JsonProperty("post_id")
    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    @JsonProperty("text")
    public String getText() {
        return text;
    }

    @JsonProperty("text")
    public void setText(String text) {
        this.text = text;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
