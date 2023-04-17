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

        Album album = new Album(title);
        albums.add(album);
        boolean artistFound = false;

        //check if artist is in databse-- if yes
       for(Artist artist : artists)
       {
           if(artist.getName().equals(artistName))
           {
               artistFound = true;
               if(artistAlbumMap.containsKey(artist))
               {
                   artistAlbumMap.get(artist).add(album);
               }
               else
               {
                   List<Album> list = new ArrayList<>();
                   list.add(album);
                   artistAlbumMap.put(artist,list);
               }
           }
       }


       //if artist not present in database
       if(artistFound==false)
       {
           Artist artist = new Artist(artistName);
           artists.add(artist);
           List<Album> list = new ArrayList<>();
           list.add(album);
           artistAlbumMap.put(artist,list);
       }

       return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{

        Song song =new Song(title,length);
        songs.add(song);
        boolean isAlbumFound = false;

        //Create and add the song to respective album
        for(Album album : albums)
        {
            if(album.getTitle().equals(albumName))
            {
               isAlbumFound = true;

                if(albumSongMap.containsKey(album))
                {
                    albumSongMap.get(album).add(song);
                }
                else
                {
                    List<Song> list = new ArrayList<>();
                    list.add(song);
                    albumSongMap.put(album,list);
                }

            }
        }

        //If the album does not exist in database, throw "Album does not exist" exception
        if(!isAlbumFound)
        {
            throw new Exception("Album does not exist");
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

        boolean isPlaylistFound = false;
        boolean isUserFound = false;
        boolean isUserCreator = false;
        boolean isUserListener = false;


        for(Playlist playlist: playlists)
        {
            if(playlist.getTitle().equals(playlistTitle))
            {   isPlaylistFound = true;
                for(User user: users)
                {
                    if(user.getMobile().equals(mobile))
                    {
                        //if user found
                        isUserFound = true;
                        if(creatorPlaylistMap.containsKey(user))
                        { //if user is creator of playlist
                             isUserCreator=true;
                             return playlist;
                        }


                        if(playlistListenerMap.containsKey(playlist))
                        {   //if user already listener
                            if(playlistListenerMap.get(playlist).contains(user))
                            {
                                isUserListener = true;
                                return playlist;
                            }

                            playlistListenerMap.get(playlist).add(user);
                        }
                        else
                        {
                            List<User> list = new ArrayList<>();
                            list.add(user);
                            playlistListenerMap.put(playlist,list);
                        }


                        if(isUserFound)
                        {
                            if(userPlaylistMap.containsKey(user))
                            {
                                userPlaylistMap.get(user).add(playlist);
                            }
                            else
                            {
                                List<Playlist> list1 = userPlaylistMap.get(user);
                                list1.add(playlist);
                                userPlaylistMap.put(user,list1);
                            }

                        }

                        return playlist;
                    }
                }
            }
        }

        if(!isUserFound)
        {
            throw new Exception("User does not exist");
        }

        if(!isPlaylistFound)
        {
            throw new Exception("Playlist does not exist");
        }

        return null;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        //The user likes the given song. The corresponding artist of the song gets auto-liked
        //A song can be liked by a user only once. If a user tried to like a song multiple times, do nothing
        //However, an artist can indirectly have multiple likes from a user, if the user has liked multiple songs of that artist.
        //If the user does not exist, throw "User does not exist" exception
        //If the song does not exist, throw "Song does not exist" exception
        //Return the song after updating

        boolean isUserfound = false;
        boolean isSongfound = false;
        boolean isUserAlreadyLikeSong = false;





        for(User user: users)
        {
            if(user.getMobile().equals(mobile))
            {
                isUserfound = true;

                for(Song song:songs)
                {
                    if(song.getTitle().equals(songTitle))
                    {
                        isSongfound = true;


                        if(songLikeMap.containsKey(song))
                        {
                            //A song can be liked by a user only once. If a user tried to like a song multiple times, do nothing
                            if(songLikeMap.get(song).contains(user))
                            {
                                isUserAlreadyLikeSong = true;
                                return song;
                            }
                            else
                            {
                                song.setLikes(song.getLikes()+1);
                                songLikeMap.get(song).add(user);
                            }
                        }
                        else
                        {
                            List<User> list = new ArrayList<>();
                            list.add(user);
                            song.setLikes(song.getLikes()+1);
                            songLikeMap.put(song,list);
                        }


                        for(Album album: albumSongMap.keySet())
                        {
                            if(albumSongMap.get(album).contains(songTitle))
                            {
                                for(Artist artist : artistAlbumMap.keySet())
                                {
                                    if(artistAlbumMap.get(artist).contains(album))
                                    {
                                        artist.setLikes(artist.getLikes()+1);
                                    }
                                }
                            }
                        }

                        return song;

                    }
                }
            }
        }

        if(!isUserfound)
        {
            throw new Exception("User does not exist");
        }

        if(!isSongfound)
        {
            throw new Exception("Song does not exist");
        }

        return null;



    }

    public String mostPopularArtist() {
         int max = 0; Artist artistMax = new Artist();
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
        Song songMax = new Song();

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
