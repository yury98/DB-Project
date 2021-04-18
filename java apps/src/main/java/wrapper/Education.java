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
        "university",
        "end-year",
        "description"
})
public class Education implements Serializable
{

    @JsonProperty("university")
    private String university;
    @JsonProperty("speciality")
    private String speciality;
    @JsonProperty("end-year")
    private Integer endYear;
    @JsonProperty("description")
    private String description;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = -863934064623276451L;

    public Education() {
    }

    /**
     *
     * @param university
     * @param speciality
     * @param descriptin
     * @param endYear
     */
    public Education(String university, String speciality, Integer endYear, String description) {
        super();
        this.university = university;
        this.speciality = speciality;
        this.endYear = endYear;
        this.description = description;
    }

    @JsonProperty("university")
    public String getUniversity() {
        return university;
    }

    @JsonProperty("university")
    public void setUniversity(String university) {
        this.university = university;
    }

    @JsonProperty("speciality")
    public String getSpeciality() {
        return speciality;
    }

    @JsonProperty("speciality")
    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    @JsonProperty("end-year")
    public Integer getEndYear() {
        return endYear;
    }

    @JsonProperty("end-year")
    public void setEndYear(Integer endYear) {
        this.endYear = endYear;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
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