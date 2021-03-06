/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.org.openeyes.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author VEDELEKT
 */
@Entity
@Table(name = "et_ophinbiometry_measurement")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EtOphinbiometryMeasurement.findAll", query = "SELECT e FROM EtOphinbiometryMeasurement e"),
    @NamedQuery(name = "EtOphinbiometryMeasurement.findById", query = "SELECT e FROM EtOphinbiometryMeasurement e WHERE e.id = :id"),
    @NamedQuery(name = "EtOphinbiometryMeasurement.findByLastModifiedDate", query = "SELECT e FROM EtOphinbiometryMeasurement e WHERE e.lastModifiedDate = :lastModifiedDate"),
    @NamedQuery(name = "EtOphinbiometryMeasurement.findByCreatedDate", query = "SELECT e FROM EtOphinbiometryMeasurement e WHERE e.createdDate = :createdDate"),
    @NamedQuery(name = "EtOphinbiometryMeasurement.findByDeleted", query = "SELECT e FROM EtOphinbiometryMeasurement e WHERE e.deleted = :deleted"),
    @NamedQuery(name = "EtOphinbiometryMeasurement.findByK1Left", query = "SELECT e FROM EtOphinbiometryMeasurement e WHERE e.k1Left = :k1Left"),
    @NamedQuery(name = "EtOphinbiometryMeasurement.findByK1Right", query = "SELECT e FROM EtOphinbiometryMeasurement e WHERE e.k1Right = :k1Right"),
    @NamedQuery(name = "EtOphinbiometryMeasurement.findByK2Left", query = "SELECT e FROM EtOphinbiometryMeasurement e WHERE e.k2Left = :k2Left"),
    @NamedQuery(name = "EtOphinbiometryMeasurement.findByK2Right", query = "SELECT e FROM EtOphinbiometryMeasurement e WHERE e.k2Right = :k2Right"),
    @NamedQuery(name = "EtOphinbiometryMeasurement.findByAxisK1Left", query = "SELECT e FROM EtOphinbiometryMeasurement e WHERE e.axisK1Left = :axisK1Left"),
    @NamedQuery(name = "EtOphinbiometryMeasurement.findByAxisK1Right", query = "SELECT e FROM EtOphinbiometryMeasurement e WHERE e.axisK1Right = :axisK1Right"),
    @NamedQuery(name = "EtOphinbiometryMeasurement.findByAxialLengthLeft", query = "SELECT e FROM EtOphinbiometryMeasurement e WHERE e.axialLengthLeft = :axialLengthLeft"),
    @NamedQuery(name = "EtOphinbiometryMeasurement.findByAxialLengthRight", query = "SELECT e FROM EtOphinbiometryMeasurement e WHERE e.axialLengthRight = :axialLengthRight"),
    @NamedQuery(name = "EtOphinbiometryMeasurement.findBySnrLeft", query = "SELECT e FROM EtOphinbiometryMeasurement e WHERE e.snrLeft = :snrLeft"),
    @NamedQuery(name = "EtOphinbiometryMeasurement.findBySnrRight", query = "SELECT e FROM EtOphinbiometryMeasurement e WHERE e.snrRight = :snrRight")})
public class EtOphinbiometryMeasurement implements Serializable {
    @Basic(optional = false)
    @Column(name = "k2_axis_left")
    private BigDecimal k2AxisLeft;
    @Basic(optional = false)
    @Column(name = "k2_axis_right")
    private BigDecimal k2AxisRight;
    @Basic(optional = false)
    @Column(name = "delta_k_left")
    private BigDecimal deltaKLeft;
    @Basic(optional = false)
    @Column(name = "delta_k_right")
    private BigDecimal deltaKRight;
    @Basic(optional = false)
    @Column(name = "delta_k_axis_left")
    private BigDecimal deltaKAxisLeft;
    @Basic(optional = false)
    @Column(name = "delta_k_axis_right")
    private BigDecimal deltaKAxisRight;
    @Basic(optional = false)
    @Column(name = "acd_left")
    private BigDecimal acdLeft;
    @Basic(optional = false)
    @Column(name = "acd_right")
    private BigDecimal acdRight;
    @Basic(optional = false)
    @Column(name = "refraction_sphere_left")
    private BigDecimal refractionSphereLeft;
    @Basic(optional = false)
    @Column(name = "refraction_sphere_right")
    private BigDecimal refractionSphereRight;
    @Basic(optional = false)
    @Column(name = "refraction_delta_left")
    private BigDecimal refractionDeltaLeft;
    @Basic(optional = false)
    @Column(name = "refraction_delta_right")
    private BigDecimal refractionDeltaRight;
    @Basic(optional = false)
    @Column(name = "refraction_axis_left")
    private BigDecimal refractionAxisLeft;
    @Basic(optional = false)
    @Column(name = "refraction_axis_right")
    private BigDecimal refractionAxisRight;
    @JoinColumn(name = "eye_status_right", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private DicomEyeStatus eyeStatusRight;
    @JoinColumn(name = "eye_status_left", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private DicomEyeStatus eyeStatusLeft;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "last_modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;
    @Basic(optional = false)
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Basic(optional = false)
    @Column(name = "deleted")
    private boolean deleted;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "k1_left")
    private BigDecimal k1Left;
    @Basic(optional = false)
    @Column(name = "k1_right")
    private BigDecimal k1Right;
    @Basic(optional = false)
    @Column(name = "k2_left")
    private BigDecimal k2Left;
    @Basic(optional = false)
    @Column(name = "k2_right")
    private BigDecimal k2Right;
    @Basic(optional = false)
    @Column(name = "axis_k1_left")
    private BigDecimal axisK1Left;
    @Basic(optional = false)
    @Column(name = "axis_k1_right")
    private BigDecimal axisK1Right;
    @Basic(optional = false)
    @Column(name = "axial_length_left")
    private BigDecimal axialLengthLeft;
    @Basic(optional = false)
    @Column(name = "axial_length_right")
    private BigDecimal axialLengthRight;
    @Basic(optional = false)
    @Column(name = "snr_left")
    private BigDecimal snrLeft;
    @Basic(optional = false)
    @Column(name = "snr_right")
    private BigDecimal snrRight;
    @Basic(optional = false)
    @Column(name = "snr_min_left")
    private BigDecimal snrMinLeft;
    @Basic(optional = false)
    @Column(name = "snr_min_right")
    private BigDecimal snrMinRight;
    @JoinColumn(name = "created_user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User createdUserId;
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Event eventId;
    @JoinColumn(name = "eye_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Eye eyeId;
    @JoinColumn(name = "last_modified_user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User lastModifiedUserId;
    @Column(name = "al_modified_left")
    private Boolean alModifiedLeft;
    @Column(name = "al_modified_right")
    private Boolean alModifiedRight;
    @Column(name = "k_modified_left")
    private Boolean kModifiedLeft;
    @Column(name = "k_modified_right")
    private Boolean kModifiedRight;

    public EtOphinbiometryMeasurement() {
    }

    public EtOphinbiometryMeasurement(Integer id) {
        this.id = id;
    }

    public EtOphinbiometryMeasurement(Integer id, Date lastModifiedDate, Date createdDate, boolean deleted, BigDecimal k1Left, BigDecimal k1Right, BigDecimal k2Left, BigDecimal k2Right, BigDecimal axisK1Left, BigDecimal axisK1Right, BigDecimal axialLengthLeft, BigDecimal axialLengthRight, BigDecimal snrLeft, BigDecimal snrRight, BigDecimal snrMinLeft, BigDecimal snrMinRight) {
        this.id = id;
        this.lastModifiedDate = lastModifiedDate;
        this.createdDate = createdDate;
        this.deleted = deleted;
        this.k1Left = k1Left;
        this.k1Right = k1Right;
        this.k2Left = k2Left;
        this.k2Right = k2Right;
        this.axisK1Left = axisK1Left;
        this.axisK1Right = axisK1Right;
        this.axialLengthLeft = axialLengthLeft;
        this.axialLengthRight = axialLengthRight;
        this.snrLeft = snrLeft;
        this.snrRight = snrRight;
        this.snrMinLeft = snrMinLeft;
        this.snrMinRight = snrMinRight;
        
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public BigDecimal getK1Left() {
        return k1Left;
    }

    public void setK1Left(BigDecimal k1Left) {
        this.k1Left = k1Left;
    }

    public BigDecimal getK1Right() {
        return k1Right;
    }

    public void setK1Right(BigDecimal k1Right) {
        this.k1Right = k1Right;
    }

    public BigDecimal getK2Left() {
        return k2Left;
    }

    public void setK2Left(BigDecimal k2Left) {
        this.k2Left = k2Left;
    }

    public BigDecimal getK2Right() {
        return k2Right;
    }

    public void setK2Right(BigDecimal k2Right) {
        this.k2Right = k2Right;
    }

    public BigDecimal getAxisK1Left() {
        return axisK1Left;
    }

    public void setAxisK1Left(BigDecimal axisK1Left) {
        this.axisK1Left = axisK1Left;
    }

    public BigDecimal getAxisK1Right() {
        return axisK1Right;
    }

    public void setAxisK1Right(BigDecimal axisK1Right) {
        this.axisK1Right = axisK1Right;
    }

    public BigDecimal getAxialLengthLeft() {
        return axialLengthLeft;
    }

    public void setAxialLengthLeft(BigDecimal axialLengthLeft) {
        this.axialLengthLeft = axialLengthLeft;
    }

    public BigDecimal getAxialLengthRight() {
        return axialLengthRight;
    }

    public void setAxialLengthRight(BigDecimal axialLengthRight) {
        this.axialLengthRight = axialLengthRight;
    }

    public BigDecimal getSnrLeft() {
        return snrLeft;
    }

    public void setSnrLeft(BigDecimal snrLeft) {
        this.snrLeft = snrLeft;
    }

    public BigDecimal getSnrRight() {
        return snrRight;
    }

    public void setSnrRight(BigDecimal snrRight) {
        this.snrRight = snrRight;
    }

    public BigDecimal getSnrMinLeft() {
        return snrMinLeft;
    }

    public void setSnrMinLeft(BigDecimal snrMinLeft) {
        this.snrMinLeft = snrMinLeft;
    }

    public BigDecimal getSnrMinRight() {
        return snrMinRight;
    }

    public void setSnrMinRight(BigDecimal snrMinRight) {
        this.snrMinRight = snrMinRight;
    }
    
    public User getCreatedUserId() {
        return createdUserId;
    }

    public void setCreatedUserId(User createdUserId) {
        this.createdUserId = createdUserId;
    }

    public Event getEventId() {
        return eventId;
    }

    public void setEventId(Event eventId) {
        this.eventId = eventId;
    }

    public Eye getEyeId() {
        return eyeId;
    }

    public void setEyeId(Eye eyeId) {
        this.eyeId = eyeId;
    }

    public User getLastModifiedUserId() {
        return lastModifiedUserId;
    }

    public void setLastModifiedUserId(User lastModifiedUserId) {
        this.lastModifiedUserId = lastModifiedUserId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EtOphinbiometryMeasurement)) {
            return false;
        }
        EtOphinbiometryMeasurement other = (EtOphinbiometryMeasurement) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "uk.org.openeyes.models.EtOphinbiometryMeasurement[ id=" + id + " ]";
    }

    public BigDecimal getK2AxisLeft() {
        return k2AxisLeft;
    }

    public void setK2AxisLeft(BigDecimal k2AxisLeft) {
        this.k2AxisLeft = k2AxisLeft;
    }

    public BigDecimal getK2AxisRight() {
        return k2AxisRight;
    }

    public void setK2AxisRight(BigDecimal k2AxisRight) {
        this.k2AxisRight = k2AxisRight;
    }

    public BigDecimal getDeltaKLeft() {
        return deltaKLeft;
    }

    public void setDeltaKLeft(BigDecimal deltaKLeft) {
        this.deltaKLeft = deltaKLeft;
    }

    public BigDecimal getDeltaKRight() {
        return deltaKRight;
    }

    public void setDeltaKRight(BigDecimal deltaKRight) {
        this.deltaKRight = deltaKRight;
    }

    public BigDecimal getDeltaKAxisLeft() {
        return deltaKAxisLeft;
    }

    public void setDeltaKAxisLeft(BigDecimal deltaKAxisLeft) {
        this.deltaKAxisLeft = deltaKAxisLeft;
    }

    public BigDecimal getDeltaKAxisRight() {
        return deltaKAxisRight;
    }

    public void setDeltaKAxisRight(BigDecimal deltaKAxisRight) {
        this.deltaKAxisRight = deltaKAxisRight;
    }

    public BigDecimal getAcdLeft() {
        return acdLeft;
    }

    public void setAcdLeft(BigDecimal acdLeft) {
        this.acdLeft = acdLeft;
    }

    public BigDecimal getAcdRight() {
        return acdRight;
    }

    public void setAcdRight(BigDecimal acdRight) {
        this.acdRight = acdRight;
    }

    public BigDecimal getRefractionSphereLeft() {
        return refractionSphereLeft;
    }

    public void setRefractionSphereLeft(BigDecimal refractionSphereLeft) {
        this.refractionSphereLeft = refractionSphereLeft;
    }

    public BigDecimal getRefractionSphereRight() {
        return refractionSphereRight;
    }

    public void setRefractionSphereRight(BigDecimal refractionSphereRight) {
        this.refractionSphereRight = refractionSphereRight;
    }

    public BigDecimal getRefractionDeltaLeft() {
        return refractionDeltaLeft;
    }

    public void setRefractionDeltaLeft(BigDecimal refractionDeltaLeft) {
        this.refractionDeltaLeft = refractionDeltaLeft;
    }

    public BigDecimal getRefractionDeltaRight() {
        return refractionDeltaRight;
    }

    public void setRefractionDeltaRight(BigDecimal refractionDeltaRight) {
        this.refractionDeltaRight = refractionDeltaRight;
    }

    public BigDecimal getRefractionAxisLeft() {
        return refractionAxisLeft;
    }

    public void setRefractionAxisLeft(BigDecimal refractionAxisLeft) {
        this.refractionAxisLeft = refractionAxisLeft;
    }

    public BigDecimal getRefractionAxisRight() {
        return refractionAxisRight;
    }

    public void setRefractionAxisRight(BigDecimal refractionAxisRight) {
        this.refractionAxisRight = refractionAxisRight;
    }

    public DicomEyeStatus getEyeStatusRight() {
        return eyeStatusRight;
    }

    public void setEyeStatusRight(DicomEyeStatus eyeStatusRight) {
        this.eyeStatusRight = eyeStatusRight;
    }

    public DicomEyeStatus getEyeStatusLeft() {
        return eyeStatusLeft;
    }

    public void setEyeStatusLeft(DicomEyeStatus eyeStatusLeft) {
        this.eyeStatusLeft = eyeStatusLeft;
    }
    
    public void setAlModifiedLeft(boolean isALModified){
        this.alModifiedLeft = isALModified;
    }
    
    public boolean getAlModifiedLeft(){
        return this.alModifiedLeft;
    }

    public void setAlModifiedRight(boolean isALModified){
        this.alModifiedRight = isALModified;
    }
    
    public boolean getAlModifiedRight(){
        return this.alModifiedRight;
    }
    
    public void setKModifiedLeft(boolean isKModified){
        this.kModifiedLeft = isKModified;
    }
    
    public boolean getKModifiedLeft(){
        return this.kModifiedLeft;
    }

    public void setKModifiedRight(boolean isKModified){
        this.kModifiedRight = isKModified;
    }
    
    public boolean getKModifiedRight(){
        return this.kModifiedRight;
    }

}
