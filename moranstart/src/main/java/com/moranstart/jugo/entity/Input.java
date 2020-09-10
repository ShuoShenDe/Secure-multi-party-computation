package com.moranstart.jugo.entity;

import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "input")
public class Input implements Serializable {
    private String id;

    private String input;
    
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

    public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", input=").append(input);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}