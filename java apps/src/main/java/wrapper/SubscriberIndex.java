package wrapper;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "index",
        "doc_type",
        "id",
        "body"
})
public class SubscriberIndex
{
    @JsonProperty("index")
    private String index;
    @JsonProperty("doc_type")
    private String docType;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("body")
    private Subscriber body;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = -5015938259191137747L;

    public SubscriberIndex() {
    }

    /**
     *
     * @param docType
     * @param index
     * @param id
     * @param body
     */
    public SubscriberIndex(String index, String docType, Integer id, Subscriber body) {
        super();
        this.index = index;
        this.docType = docType;
        this.id = id;
        this.body = body;
    }

    @JsonProperty("index")
    public String getIndex() {
        return index;
    }

    @JsonProperty("index")
    public void setIndex(String index) {
        this.index = index;
    }

    @JsonProperty("doc_type")
    public String getDocType() {
        return docType;
    }

    @JsonProperty("doc_type")
    public void setDocType(String docType) {
        this.docType = docType;
    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("body")
    public Subscriber getBody() {
        return body;
    }

    @JsonProperty("body")
    public void setBody(Subscriber body) {
        this.body = body;
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