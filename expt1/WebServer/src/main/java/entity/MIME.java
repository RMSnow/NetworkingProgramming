package entity;

/**
 * Created by snow on 2018/4/21.
 */
public enum MIME {
    CSS("CSS"), //
    GIF("GIF"), //
    HTM("HTM"), //
    HTML("HTML"), //
    ICO("ICO"), //
    JPG("JPG"), //
    JPEG("JPEG"), //
    PNG("PNG"), //
    TXT("TXT"), //
    XML("XML"); //

    private final String extension;

    MIME(String extension) {
        this.extension = extension;
    }

    @Override
    public String toString() {
        switch (this) {
            case CSS:
                return "Content-Type: text/css";
            case GIF:
                return "Content-Type: image/gif";
            case HTM:
            case HTML:
                return "Content-Type: text/html";
            case ICO:
                return "Content-Type: image/gif";
            case JPG:
            case JPEG:
                return "Content-Type: image/jpeg";
            case PNG:
                return "Content-Type: image/png";
            case TXT:
                return "Content-type: text/plain";
            case XML:
                return "Content-type: text/xml";
            default:
                return null;
        }
    }
}
