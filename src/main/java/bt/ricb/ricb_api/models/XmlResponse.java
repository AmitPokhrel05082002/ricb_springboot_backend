package bt.ricb.ricb_api.models;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "string", namespace = "http://tempuri.org/")
public class XmlResponse {
    private String content;

    @XmlElement(name = "string")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
