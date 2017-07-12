
package com.times.places.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.times.places.objects.Base;


public class Geometry extends Base {

    @SerializedName("location")
    @Expose
    private Location location;

    /**
     *
     * @return
     *     The location
     */
    public Location getLocation() {
        return location;
    }

    /**
     *
     * @param location
     *     The location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

}
