package it.uniroma2.dicii.isw2.deliverable2.entities;

import java.util.Date;
import java.util.List;

/**
 * A single bug, with information about referring ticket and lifecycle.
 */
public class Bug extends ExportableAsDatasetRecord {
    private Ticket ticket;
    private BugLifecycle bugLifecycle;

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public BugLifecycle getBugLifecycle() {
        return bugLifecycle;
    }

    public void setBugLifecycle(BugLifecycle bugLifecycle) {
        this.bugLifecycle = bugLifecycle;
    }

    /**
     * Bug ID is actually considered as same ID of the ticket
     *
     * @return
     */
    public String getID() {
        return ticket.getID();
    }

    public Date getCreationTimestamp() {
        return this.ticket.getCreationTimestamp();
    }

    @Override
    public List<List<String>> getDatasetAttributes() {
        this.setDatasetAttributes("ReferringTicketID", "IV", "OV", "FV");
        return this.datasetAttributes;
    }

    @Override
    public List<List<String>> getDatasetRecord() {
        this.setDatasetRecord(this.ticket != null ? this.ticket.getID() : "N.A.",
                this.bugLifecycle.getIV() != null ? this.bugLifecycle.getIV().getSortedID() : "N.A.",
                this.bugLifecycle.getOV() != null ? this.bugLifecycle.getOV().getSortedID() : "N.A.",
                this.bugLifecycle.getFV() != null ? this.bugLifecycle.getFV().getSortedID() : "N.A.");
        return this.datasetRecord;
    }
}
