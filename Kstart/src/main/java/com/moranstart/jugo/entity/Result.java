package com.moranstart.jugo.entity;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "result")
public class Result implements Serializable {
    private String id;

    private String content;

    private Date cre_time;

    private Integer type;

    private static final long serialVersionUID = 1L;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * @return content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content
     */
    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    /**
     * @return cre_time
     */
    public Date getCre_time() {
        return cre_time;
    }

    /**
     * @param cre_time
     */
    public void setCre_time(Date cre_time) {
        this.cre_time = cre_time;
    }

    /**
     * @return type
     */
    public Integer getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", content=").append(content);
        sb.append(", cre_time=").append(cre_time);
        sb.append(", type=").append(type);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}