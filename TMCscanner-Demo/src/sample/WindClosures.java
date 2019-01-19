package sample;


/**
 * WINDCLOSURES CLASS
 * Stores a list of sensors and the message of the road to close if the sensor hits EBOR.
 *
 */
public class WindClosures {

    private String messageName = "";
    private Boolean hasEBOR = false;



    private Integer eborCount = 0;

    public Boolean getHasEBOR() {
        return hasEBOR;
    }

    public void setHasEBOR(Boolean hasEBOR) {
        this.hasEBOR = hasEBOR;
    }

    public Integer getEborCount() {
        return eborCount;
    }

    public void setEborCount(Integer eborCount) {
        this.eborCount = eborCount;
    }

    public void incrementEborCount(){
        this.eborCount = this.eborCount++;
    }
    public void decrementEborCount(){
        this.eborCount = this.eborCount--;
    }


    public String getMessageName() {
        return messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }



}
