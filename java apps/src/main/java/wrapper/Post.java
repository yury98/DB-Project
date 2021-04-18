package wrapper;

import java.io.Serializable;
import java.util.HashMap;
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
        "category",
        "text",
        "video-href",
        "image-href",
        "view-num"
})
public class Post implements Serializable
{

    @JsonProperty("date")
    private String date;
    @JsonProperty("category")
    private String category;
    @JsonProperty("text")
    private String text;
    @JsonProperty("video-href")
    private String videoHref;
    @JsonProperty("image-href")
    private String imageHref;
    @JsonProperty("view-num")
    private Integer viewNum;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = 3083946903752462101L;

    public Post() {
    }

    /**
     *
     * @param date
     * @param viewNum
     * @param imageHref
     * @param text
     * @param videoHref
     * @param category
     */
    public Post(String date, String category, String text, String videoHref, String imageHref, Integer viewNum) {
        super();
        this.date = date;
        this.category = category;
        this.text = text;
        this.videoHref = videoHref;
        this.imageHref = imageHref;
        this.viewNum = viewNum;
    }

    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(String date) {
        this.date = date;
    }

    @JsonProperty("category")
    public String getCategory() {
        return category;
    }

    @JsonProperty("category")
    public void setCategory(String category) {
        this.category = category;
    }

    @JsonProperty("text")
    public String getText() {
        return text;
    }

    @JsonProperty("text")
    public void setText(String text) {
        this.text = text;
    }

    @JsonProperty("video-href")
    public String getVideoHref() {
        return videoHref;
    }

    @JsonProperty("video-href")
    public void setVideoHref(String videoHref) {
        this.videoHref = videoHref;
    }

    @JsonProperty("image-href")
    public String getImageHref() {
        return imageHref;
    }

    @JsonProperty("image-href")
    public void setImageHref(String imageHref) {
        this.imageHref = imageHref;
    }

    @JsonProperty("view-num")
    public Integer getViewNum() {
        return viewNum;
    }

    @JsonProperty("view-num")
    public void setViewNum(Integer viewNum) {
        this.viewNum = viewNum;
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