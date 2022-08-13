package de.backxtar.dbController;

public class HttpPost {
    /* Global Variables */
    private String auth0;
    private int content_length;
    private String header, body;

    /**
     * Set auth0 key for the object.
     * @param auth0 key as String.
     */
    public void setAuth0(String auth0) {
        this.auth0 = auth0;
    }

    /**
     * Getter for the auth key.
     * @return auth0 key as String.
     */
    public String getAuth0() {
        return auth0;
    }

    /**
     * Set the body's content length
     * @param content_length as int.
     */
    public void setContent_length(int content_length) {
        this.content_length = content_length;
    }

    /**
     * Get the body's content length.
     * @return length as int.
     */
    public int getContent_length() {
        return content_length;
    }

    /**
     * Set httpPost's header.
     * @param header as String.
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * Get the httpPost's header.
     * @return header as String.
     */
    public String getHeader() {
        return header;
    }

    /**
     * Set the httpPost's body.
     * @param body as String.
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Get data body of the httpPost.
     * @return body as String.
     */
    public String getBody() {
        return body;
    }
}
