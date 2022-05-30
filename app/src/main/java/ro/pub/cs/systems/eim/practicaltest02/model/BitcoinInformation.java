package ro.pub.cs.systems.eim.practicaltest02.model;

public class BitcoinInformation {

    private String updatedTag;
    private float rate;

    public BitcoinInformation() {
        this.updatedTag = null;
        this.rate = 0;
    }

    public BitcoinInformation(String updatedTag, float rate) {
        this.updatedTag = updatedTag;
        this.rate = rate;
    }

    public String getUpdatedTag() {
        return updatedTag;
    }

    public void setUpdatedTag(String updatedTag) {
        this.updatedTag = updatedTag;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "resultInformation{" +
                "updated='" + updatedTag + '\'' +
                ", rate='" + rate + '\'' +
                '}';
    }
}
