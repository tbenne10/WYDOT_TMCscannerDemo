package sample;

import java.util.ArrayList;
import java.util.Objects;

//SENSOR CLASS//
//CONTAINS DATA ON EACH SENSOR//

//Csv1 Contains the following:
// Siteid,DtTm,AirTemp,Dewpoint,Rh,SpdAvg,SpdGust,DirMin,DirAvg,
// DirMax,Pressure,PcIntens,PcType,PcRate,PcAccum,Visibility

//CSV2 contains the following:
// Siteid,senid,DtTm,sfcond,sftemp,frztemp,chemfactor,chempct,depth,icepct,subsftemp,waterlevel


public class Sensor {

    /**
     * The following variables are from CSV 1
     */
    private String Siteid = null;
    private ArrayList<String> District = new ArrayList<>();
    private String DtTm = null;
    private String AirTemp = null;
    private String Dewpoint = null;
    private String Rh = null;
    private String SpdAvg = null;
    private String SpdGust = null;
    private String DirMin = null;
    private String DirAvg = null;
    private String DirMax = null;
    private String Pressure = null;
    private String PcIntens = null;
    private String PcType = null;
    private String PcRate = null;
    private String PcAccum = null;
    private String Visibility = null;



    /**
     * The following variables are from CSV 2, and sensor locations may have multiple sensors.
     */
    private ArrayList<String> senId = new ArrayList<>();
    private ArrayList<String> sfcond = new ArrayList<>();
    private ArrayList<String> sftemp = new ArrayList<>();
    private ArrayList<String> frztemp = new ArrayList<>();
    private ArrayList<String> chemfactor = new ArrayList<>();
    private ArrayList<String> chempct = new ArrayList<>();
    private ArrayList<String> depth = new ArrayList<>();
    private ArrayList<String> icepct = new ArrayList<>();
    private ArrayList<String> subsftemp = new ArrayList<>();
    private ArrayList<String> waterlevel = new ArrayList<>();


    /**
     * The following variables are from sensor_locations, and specifies what gets output on the GUI.
     */
    private String town_name = null;
    private String sensor_location = null;
    private ArrayList<String> additional_info = new ArrayList<>();


    /**
     * Default Constructor
     */
    Sensor(){}


    /**
     * Copy Constructor
     */
    Sensor(Sensor S){
        Siteid = S.Siteid;
        District = S.District;
        DtTm = S.DtTm;
        AirTemp = S.AirTemp;
        Dewpoint = S.Dewpoint;
        Rh = S.Rh;
        SpdAvg = S.SpdAvg;
        SpdGust = S.SpdGust;
        DirMin = S.DirMin;
        DirAvg = S.DirAvg;
        DirMax = S.DirMax;
        Pressure = S.Pressure;
        PcIntens = S.PcIntens;
        PcType = S.PcType;
        PcRate = S.PcRate;
        PcAccum = S.PcAccum;
        Visibility = S.Visibility;
        senId = S.senId;
        sfcond = S.sfcond;
        sftemp = S.sftemp;
        frztemp = S.frztemp;
        chemfactor = S.chemfactor;
        chempct = S.chempct;
        depth = S.depth;
        icepct = S.icepct;
        subsftemp = S.subsftemp;
        waterlevel = S.waterlevel;
        town_name = S.town_name;
        sensor_location = S.sensor_location;
        additional_info = S.additional_info;
    }

    /**
     * Getter and setter declarations
     */

    public ArrayList<String> getAdditional_info() {
        return additional_info;
    }

    public void setTown_name(String town_name) {
        this.town_name = town_name;
    }

    public void setSensor_location(String sensor_location) {
        this.sensor_location = sensor_location;
    }

    /**
    * Necessary for hashMap
    * */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sensor sensor = (Sensor) o;
        return Objects.equals(Siteid, sensor.Siteid) &&
                Objects.equals(District, sensor.District) &&
                Objects.equals(DtTm, sensor.DtTm) &&
                Objects.equals(AirTemp, sensor.AirTemp) &&
                Objects.equals(Dewpoint, sensor.Dewpoint) &&
                Objects.equals(Rh, sensor.Rh) &&
                Objects.equals(SpdAvg, sensor.SpdAvg) &&
                Objects.equals(SpdGust, sensor.SpdGust) &&
                Objects.equals(DirMin, sensor.DirMin) &&
                Objects.equals(DirAvg, sensor.DirAvg) &&
                Objects.equals(DirMax, sensor.DirMax) &&
                Objects.equals(Pressure, sensor.Pressure) &&
                Objects.equals(PcIntens, sensor.PcIntens) &&
                Objects.equals(PcType, sensor.PcType) &&
                Objects.equals(PcRate, sensor.PcRate) &&
                Objects.equals(PcAccum, sensor.PcAccum) &&
                Objects.equals(Visibility, sensor.Visibility) &&
                Objects.equals(senId, sensor.senId) &&
                Objects.equals(sfcond, sensor.sfcond) &&
                Objects.equals(sftemp, sensor.sftemp) &&
                Objects.equals(frztemp, sensor.frztemp) &&
                Objects.equals(chemfactor, sensor.chemfactor) &&
                Objects.equals(chempct, sensor.chempct) &&
                Objects.equals(depth, sensor.depth) &&
                Objects.equals(icepct, sensor.icepct) &&
                Objects.equals(subsftemp, sensor.subsftemp) &&
                Objects.equals(waterlevel, sensor.waterlevel) &&
                Objects.equals(town_name, sensor.town_name) &&
                Objects.equals(sensor_location, sensor.sensor_location) &&
                Objects.equals(additional_info, sensor.additional_info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Siteid, District, DtTm, AirTemp, Dewpoint,
                Rh, SpdAvg, SpdGust, DirMin, DirAvg, DirMax,
                Pressure, PcIntens, PcType, PcRate, PcAccum,
                Visibility, senId, sfcond, sftemp, frztemp,
                chemfactor, chempct, depth, icepct, subsftemp,
                waterlevel, town_name, sensor_location,
                additional_info);
    }


    public String getTown_name() {
        return town_name;
    }

    public String getSensor_location() {
        return sensor_location;
    }



    public ArrayList<String> getDistrict() {
        return District;
    }

    public void setDistrict(ArrayList<String> district) {
        District = district;
    }

    public String getSiteid() {
        return Siteid;
    }

    public void setSiteid(String siteid) {
        Siteid = siteid;
    }

    public String getDtTm() {
        return DtTm;
    }

    public void setDtTm(String dtTm) {
        DtTm = dtTm;
    }

    public String getAirTemp() {
        return AirTemp;
    }

    public void setAirTemp(String airTemp) {
        AirTemp = airTemp;
    }

    public String getDewpoint() {
        return Dewpoint;
    }

    public void setDewpoint(String dewpoint) {
        Dewpoint = dewpoint;
    }

    public String getRh() {
        return Rh;
    }

    public void setRh(String rh) {
        Rh = rh;
    }

    public String getSpdAvg() {
        return SpdAvg;
    }

    public void setSpdAvg(String spdAvg) {
        SpdAvg = spdAvg;
    }

    public String getSpdGust() {
        return SpdGust;
    }

    public void setSpdGust(String spdGust) {
        SpdGust = spdGust;
    }

    public String getDirMin() {
        return DirMin;
    }

    public void setDirMin(String dirMin) {
        DirMin = dirMin;
    }

    public String getDirAvg() {
        return DirAvg;
    }

    public void setDirAvg(String dirAvg) {
        DirAvg = dirAvg;
    }

    public String getDirMax() {
        return DirMax;
    }

    public void setDirMax(String dirMax) {
        DirMax = dirMax;
    }

    public String getPressure() {
        return Pressure;
    }

    public void setPressure(String pressure) {
        Pressure = pressure;
    }

    public String getPcIntens() {
        return PcIntens;
    }

    public void setPcIntens(String pcIntens) {
        PcIntens = pcIntens;
    }

    public String getPcType() {
        return PcType;
    }

    public void setPcType(String pcType) {
        PcType = pcType;
    }

    public String getPcRate() {
        return PcRate;
    }

    public void setPcRate(String pcRate) {
        PcRate = pcRate;
    }

    public String getPcAccum() {
        return PcAccum;
    }

    public void setPcAccum(String pcAccum) {
        PcAccum = pcAccum;
    }

    public String getVisibility() {
        return Visibility;
    }

    public void setVisibility(String visibility) {
        Visibility = visibility;
    }

    public ArrayList<String> getSenId() {
        return senId;
    }

    public ArrayList<String> getSfcond() {
        return sfcond;
    }

    public ArrayList<String> getSftemp() {
        return sftemp;
    }

    public ArrayList<String> getFrztemp() {
        return frztemp;
    }

    public ArrayList<String> getChemfactor() {
        return chemfactor;
    }

    public ArrayList<String> getChempct() {
        return chempct;
    }


    public ArrayList<String> getDepth() {
        return depth;
    }


    public ArrayList<String> getIcepct() {
        return icepct;
    }


    public ArrayList<String> getSubsftemp() {
        return subsftemp;
    }

    public ArrayList<String> getWaterlevel() {
        return waterlevel;
    }



    public void setSenId(ArrayList<String> senId) {
        this.senId = senId;
    }

    public void setSfcond(ArrayList<String> sfcond) {
        this.sfcond = sfcond;
    }

    public void setSftemp(ArrayList<String> sftemp) {
        this.sftemp = sftemp;
    }

    public void setFrztemp(ArrayList<String> frztemp) {
        this.frztemp = frztemp;
    }

    public void setChemfactor(ArrayList<String> chemfactor) {
        this.chemfactor = chemfactor;
    }

    public void setChempct(ArrayList<String> chempct) {
        this.chempct = chempct;
    }

    public void setDepth(ArrayList<String> depth) {
        this.depth = depth;
    }

    public void setIcepct(ArrayList<String> icepct) {
        this.icepct = icepct;
    }

    public void setSubsftemp(ArrayList<String> subsftemp) {
        this.subsftemp = subsftemp;
    }

    public void setWaterlevel(ArrayList<String> waterlevel) {
        this.waterlevel = waterlevel;
    }


    public void setAdditional_info(ArrayList<String> additional_info) {
        this.additional_info = additional_info;
    }



}
