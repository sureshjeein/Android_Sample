package com.jpmc.samplealbum.samplealbumlist.network;

import com.jpmc.samplealbum.samplealbumlist.SampleAlbum;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface GetAlbumDataService {

    @GET("/albums")
    Call<List<SampleAlbum>> getAllAlbums();

}
