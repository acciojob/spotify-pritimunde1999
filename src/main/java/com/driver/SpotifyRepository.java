package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User(name,mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {

        Artist artist1 = null;
       for(Artist artist: artists)
       {
           if(artist.getName().equals(artistName))
           {
               artist1 = artist;
           }
       }

       if(artist1.equals(null))
       {
           artist1 = new Artist(artistName);
           artists.add(artist1);
       }

       Album album = new Album(title);

       if(artistAlbumMap.containsKey(artist1))
       {
           artistAlbumMap.get(artist1).add(album);
       }
       else
       {
           List<Album> list = new ArrayList<>();
           list.add(album);
           artistAlbumMap.put(artist1,list);
       }

       return album;

    }

    public Song createSong(String title, String albumName, int length) throws Exception{

        Song song =new Song(title,length);
        songs.add(song);
        Album albumN = new Album();
        for(Album album : albums)
        {
            if(album.getTitle().equals(albumName))
            {
                albumN = album;
            }
        }

        if(albumN==null)
        {
            throw new Exception("Album does not exist");
        }

        if(albumSongMap.containsKey(albumN))
        {
            albumSongMap.get(albumN).add(song);
        }
        else
        {
            if(albumN!=null)
            {
                List<Song> list = new ArrayList<>();
                list.add(song);
                albumSongMap.put(albumN,list);
            }
        }



        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {

        //If the user does not exist, throw "User does not exist" exception
        User user1= null;
        for(User user : users)
        {
            if(user.getMobile().equals(mobile))
            {
                user1 = user;
            }
        }

        //user not exist
        if(user1==null)
        {
            throw new Exception("User does not exist");
        }


        //Create a playlist with given title and add all songs having the given length in the database to that playlist
        List<Song> songList = new ArrayList<>();
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);

        for(Song song : songs)
        {
            if(song.getLength()==length)
            {
                songList.add(song);
            }
        }
        if(!playlistSongMap.containsKey(playlist))
        {
            playlistSongMap.put(playlist,songList);
        }
        else
        {
            playlistSongMap.get(playlist).addAll(songList);
        }

        //The creater of the playlist will be the given user and will also be the only listener at the time of playlist creation
        creatorPlaylistMap.put(user1,playlist);
        List<User> userList = new ArrayList<>();
        userList.add(user1);
        playlistListenerMap.put(playlist,userList);

        if(userPlaylistMap.containsKey(user1))
        {
            userPlaylistMap.get(user1).add(playlist);
        }
        else
        {
            List<Playlist> list = new ArrayList<>();
            list.add(playlist);
            userPlaylistMap.put(user1,list);
        }



        return playlist;
    }



    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {

        //If the user does not exist, throw "User does not exist" exception
        User user1= null;
        for(User user : users)
        {
            if(user.getMobile().equals(mobile))
            {
                user1 = user;
            }
        }

        //user not exist
        if(user1==null)
        {
            throw new Exception("User does not exist");
        }



        //Create a playlist with given title and add all songs having the given titles in the database to that playlist
        List<Song> songList = new ArrayList<>();
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);

        for(Song song : songs)
        {
            for(String songTitle : songTitles)
            {
                if(song.getTitle().equals(songTitle))
                {
                    songList.add(song);
                }
            }
        }
        if(!playlistSongMap.containsKey(playlist))
        {
            playlistSongMap.put(playlist,songList);
        }
        else
        {
            playlistSongMap.get(playlist).addAll(songList);
        }



        //The creater of the playlist will be the given user and will also be the only listener at the time of playlist creation
        creatorPlaylistMap.put(user1,playlist);
        List<User> userList = new ArrayList<>();
        userList.add(user1);
        playlistListenerMap.put(playlist,userList);

        if(userPlaylistMap.containsKey(user1))
        {
            userPlaylistMap.get(user1).add(playlist);
        }
        else
        {
            List<Playlist> list = new ArrayList<>();
            list.add(playlist);
            userPlaylistMap.put(user1,list);
        }



        return playlist;

    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {

        //Find the playlist with given title and add user as listener of that playlist and update user accordingly
        //If the user is creater or already a listener, do nothing

        //If the playlist does not exists, throw "Playlist does not exist" exception
        // Return the playlist after updating


        //If the user does not exist, throw "User does not exist" exception
        User user1= null;
        for(User user : users)
        {
            if(user.getMobile().equals(mobile))
            {
                user1 = user;
            }
        }

        //user not exist
        if(user1==null)
        {
            throw new Exception("User does not exist");
        }

        //Find the playlist with given title and add user as listener of that playlist and update user accordingly
        Playlist playlist = null;

        for(Playlist playlist1 : playlists)
        {
            if(playlist1.getTitle().equals(playlistTitle))
            {
                playlist = playlist1;
            }
        }

        if(playlist==null)
        {
            throw new Exception("Playlist does not exist");
        }

        if(playlistListenerMap.containsKey(playlist))
        {
            playlistListenerMap.get(playlist).add(user1);
        }

        if(userPlaylistMap.containsKey(user1))
        {
            userPlaylistMap.get(user1).add(playlist);
        }

        return playlist;

    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        //The user likes the given song. The corresponding artist of the song gets auto-liked
        //A song can be liked by a user only once. If a user tried to like a song multiple times, do nothing
        //However, an artist can indirectly have multiple likes from a user, if the user has liked multiple songs of that artist.


        //Return the song after updating


        //If the user does not exist, throw "User does not exist" exception
        User user1= null;
        for(User user : users)
        {
            if(user.getMobile().equals(mobile))
            {
                user1 = user;
            }
        }

        //user not exist
        if(user1==null)
        {
            throw new Exception("User does not exist");
        }


        //If the song does not exist, throw "Song does not exist" exception
        Song song1 = null;
        for(Song song : songs)
        {
            if(song.getTitle().equals(songTitle))
            {
                song1 = song;
            }
        }

        if(song1==null)
        {
            throw new Exception("Song does not exist");
        }




        //The user likes the given song. The corresponding artist of the song gets auto-liked

        //song liked by users list
        if(songLikeMap.containsKey(song1))
        {
            if(!songLikeMap.get(song1).contains(user1))
            {
                songLikeMap.get(song1).add(user1);
                song1.setLikes(song1.getLikes()+1);



                Album album1 = null;
                for(Album album : albumSongMap.keySet())
                {
                    List<Song> songList = albumSongMap.get(album);
                    for(Song songsss : songList)
                    {
                        if(song1.equals(songsss))
                        {
                            album1 = album;
                        }
                    }
                }


                for(Artist artist : artistAlbumMap.keySet())
                {
                    List<Album> albums = artistAlbumMap.get(artist);
                    for (Album album2 : albums)
                    {

                        if(album1.equals(album2))
                        {
                           artist.setLikes(artist.getLikes()+1);
                        }
                    }

                }

            }
        }
        else
        {
            List<User> list = new ArrayList<>();
            list.add(user1);
            songLikeMap.put(song1,list);
        }


        return song1;

    }

    public String mostPopularArtist() {
         int max = 0; Artist artistMax = null;
         for(Artist artist: artists)
         {
             if(artist.getLikes()>max)
             {
                max = artist.getLikes();
                artistMax = artist;
             }
         }

         return artistMax.getName();
    }

    public String mostPopularSong() {

        int max =0;
        Song songMax = null;

        for(Song song : songs)
        {
            if(song.getLikes()>max)
            {
                max = song.getLikes();
                songMax = song;
            }
        }

        return songMax.getTitle();
    }
}
