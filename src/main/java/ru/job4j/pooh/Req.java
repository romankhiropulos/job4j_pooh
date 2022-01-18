package ru.job4j.pooh;

public class Req {

    private final String httpRequestType;

    private final String poohMode;

    private final String sourceName;

    private final String param;

    public Req(String httpRequestType, String poohMode, String sourceName, String param) {
        this.httpRequestType = httpRequestType;
        this.poohMode = poohMode;
        this.sourceName = sourceName;
        this.param = param;
    }

    public static Req of(String content) {
        String ls = System.lineSeparator();
        String[] arrContent = content.split(ls);
        String[] arrContentFirstLine = arrContent[0].split(" ");
        String[] arrContentFirstLinePath = arrContentFirstLine[1].split("/");
        String httpRequestType = arrContentFirstLine[0];
        String poohMode = arrContentFirstLinePath[1];
        String sourceName = arrContentFirstLinePath[2];
        String param = "GET".equals(httpRequestType) ? arrContentFirstLinePath.length > 3 ? arrContentFirstLinePath[3]
                                                                                          : ""
                                                     : arrContent[arrContent.length - 1];
        return new Req(httpRequestType, poohMode, sourceName, param);
    }

    public String httpRequestType() {
        return httpRequestType;
    }

    public String getPoohMode() {
        return poohMode;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getParam() {
        return param;
    }
}
