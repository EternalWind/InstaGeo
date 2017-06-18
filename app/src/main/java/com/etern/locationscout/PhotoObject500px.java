package com.etern.locationscout;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by etern on 6/12/2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class PhotoObject500px {
    public long id;
    public long user_id;
    public String name;
    public String description;
    public String camera;
    public String lens;
    public String focal_length;
    public String iso;
    public String shutter_speed;
    public String aperture;
    public int times_viewed;
    public float rating;
    public int status;
    public String created_at;
    public int category;
    public String location;
    public double latitude;
    public double longitude;
    public String taken_at;
    public int hi_res_uploaded;
    public boolean for_sale;
    public int width;
    public int height;
    public int votes_count;
    public int favorites_count;
    public int comment_count;
    public boolean nsfw;
    public int sales_count;
    public String for_sale_date;
    public float highest_rating;
    public String highest_rating_date;
    public int license_type;
    public int converted;
    public int collections_count;
    public int crop_version;
    public boolean privacy;
    public boolean profile;
    public String image_url;
}
