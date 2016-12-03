package com.mahmoud.popularmovies.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MoviesModel {

    @SerializedName("page")
    @Expose
    private String page;
    @SerializedName("results")
    @Expose
    private List<MoviesResultModel> results = new ArrayList<MoviesResultModel>();

    /**
     * 
     * @return
     *     The page
     */
    public String getPage() {
        return page;
    }

    /**
     * 
     * @param page
     *     The page
     */
    public void setPage(String page) {
        this.page = page;
    }

    /**
     * 
     * @return
     *     The results
     */
    public List<MoviesResultModel> getResults() {
        return results;
    }

    /**
     * 
     * @param results
     *     The results
     */
    public void setResults(List<MoviesResultModel> results) {
        this.results = results;
    }

}
