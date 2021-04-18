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
        "reg_date",
        "name",
        "image-href",
        "birthday",
        "email",
        "profession",
        "education",
        "comment"
})
public class Subscriber implements Serializable
{

    @JsonProperty("reg_date")
    private String regDate;
    @JsonProperty("name")
    private String name;
    @JsonProperty("image-href")
    private String imageHref;
    @JsonProperty("birthday")
    private String birthday;
    @JsonProperty("email")
    private String email;
    @JsonProperty("profession")
    private String profession;
    @JsonProperty("education")
    private Education education;
    @JsonProperty("comment")
    private List<Comment> comment;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = 7370722717059904126L;

    public Subscriber() {
    }

    /**
     *
     * @param birthday
     * @param profession
     * @param education
     * @param imageHref
     * @param name
     * @param regDate
     * @param comment
     * @param email
     */
    public Subscriber(String regDate, String name, String imageHref, String birthday, String email, String profession, Education education, List<Comment> comment) {
        super();
        this.regDate = regDate;
        this.name = name;
        this.imageHref = imageHref;
        this.birthday = birthday;
        this.email = email;
        this.profession = profession;
        this.education = education;
        this.comment = comment;
    }

    @JsonProperty("reg_date")
    public String getRegDate() {
        return regDate;
    }

    @JsonProperty("reg_date")
    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("image-href")
    public String getImageHref() {
        return imageHref;
    }

    @JsonProperty("image-href")
    public void setImageHref(String imageHref) {
        this.imageHref = imageHref;
    }

    @JsonProperty("birthday")
    public String getBirthday() {
        return birthday;
    }

    @JsonProperty("birthday")
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("profession")
    public String getProfession() {
        return profession;
    }

    @JsonProperty("profession")
    public void setProfession(String profession) {
        this.profession = profession;
    }

    @JsonProperty("education")
    public Education getEducation() {
        return education;
    }

    @JsonProperty("education")
    public void setEducation(Education education) {
        this.education = education;
    }

    @JsonProperty("comment")
    public List<Comment> getComment() {
        return comment;
    }

    @JsonProperty("comment")
    public void setComment(List<Comment> comment) {
        this.comment = comment;
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
