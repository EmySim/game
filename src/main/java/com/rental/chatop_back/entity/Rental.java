package com.rental.chatop_back.entity;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Entity class representing a rental property.
 */
@Entity
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private double price;
    private String location;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    // Getters and setters

    /**
     * Gets the ID of the rental.
     *
     * @return the ID of the rental.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the rental.
     *
     * @param id the ID of the rental.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the rental.
     *
     * @return the name of the rental.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the rental.
     *
     * @param name the name of the rental.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the rental.
     *
     * @return the description of the rental.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the rental.
     *
     * @param description the description of the rental.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the price of the rental.
     *
     * @return the price of the rental.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the price of the rental.
     *
     * @param price the price of the rental.
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Gets the location of the rental.
     *
     * @return the location of the rental.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location of the rental.
     *
     * @param location the location of the rental.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets the creation date of the rental.
     *
     * @return the creation date of the rental.
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation date of the rental.
     *
     * @param createdAt the creation date of the rental.
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the last updated date of the rental.
     *
     * @return the last updated date of the rental.
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the last updated date of the rental.
     *
     * @param updatedAt the last updated date of the rental.
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
