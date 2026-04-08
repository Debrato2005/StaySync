package com.staysync.model;
import java.io.Serializable;

public class Guest implements Serializable {
    private static final long serialVersionUID = 1L;
    private int guestId;
    private String name;
    private String contact;
    private String idProof;

    public Guest(int guestId, String name, String contact, String idProof) {
        this.guestId = guestId;
        this.name = name;
        this.contact = contact;
        this.idProof = idProof;
    }

    public int getGuestId() { return guestId; }
    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getIdProof() { return idProof; }
    @Override
public String toString() {
    return name + " (ID: " + guestId + ")";
}
}