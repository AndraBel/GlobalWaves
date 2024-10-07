# <span style="color:Darkorange">GlobalWaves Project - Stage 3 - Analytics & Recommendations</span>

For this stage, I used my previous implementation from the earlier stages.

## <span style="color:Magenta">Main</span>

In main, I didn't add much, just the new commands from this stage. After
the test was completed, I went through all the users and calculated their
current status, then called the end program function, which displays the
monetization for artists based on user listens.

## <span style="color:Magenta">Library</span>

I'll start with notifications since that was the first part of this stage. I
used the Observer design pattern, where the user is an observer and the artist
or host is a subject. The observer list is updated when a user subscribes or
unsubscribes. Whenever the artist or host performs an action, I call the
notifyObserver function, which updates the notification results array for
each observer (user). Another design pattern I implemented is Strategy, used
for recommendation-related methods. Depending on the command, I set the
appropriate strategy and execute it. I used the same approach for wrapped
commands, where I also implemented the Strategy pattern. For wrapped, I used
the UsersHistory class to store each user's history. Everything a user
listened to in the player is counted in various hashmaps within this class.
There are also methods for premium and free monetization. For premium, whenever
a user cancels their subscription, they pay for all the songs listened to during
the subscription period. For free, after an ad finishes (tracked in the player),
artists listened to between two ads are paid.

## <span style="color:Magenta">User</span>

I added several methods related to navigating between pages, buying artist
merch, and loading various recommendations. I also made some changes to the
album search functionality. Previously, I only stored the album name after
search and selection, then searched for it again in the library to play it.
Now, I store the entire album to allow searches by both album and artist.

## <span style="color:Magenta">Player</span>

In this class, I added a counter for songs listened to between ads, paying
attention to special cases where the song isn't finished and is followed by a
load, or if an album was loaded before the ad break and I need to remember the
song where it left off to resume after the advertisement.

### <span style="color:Aquamarine">User </span> implements:

    > SearchBarCommands
    > PlayerCommands
    > PlaylistCommands
    > Observer

### <span style="color:Aquamarine">Library </span> implements:

    > GeneralStatistics
    > Uses Singleton design pattern for a single instance
    > Uses Observer design pattern for notifications
    > Uses Strategy design pattern for recommendations
    > Uses Strategy design pattern for wrapped

### <span style="color:Aquamarine">Song, Podcast, AudioFilesCollection:

    > AudioFiles

### <span style="color:Aquamarine">Album, Playlist</span> extend:

    > AudioFilesCollection

### <span style="color:Aquamarine">ArtistPage, HomePage, HostPage,

### <span style="color:Aquamarine">LikedContentPage</span> extend:

    > Page

### <span style="color:Aquamarine">ArtistPage, HomePage, HostPage,

### <span style="color:Aquamarine">PagePrinter</span> implement:

    > PageVisitor
    > Uses Visitor design pattern to display page content

### <span style="color:Aquamarine">Announcement, Event, Merch</span> extend:

    > Publicity

### <span style="color:Aquamarine">Notifications </span> implements:

    > Observer

### <span style="color:Aquamarine">Artist, Host </span> implement:

    > Subject

### <span style="color:Aquamarine">FansPlaylistRecommendation,

### <span style="color:Aquamarine">RandomPlaylistRecommendation,

### <span style="color:Aquamarine">RandomSongRecommendation </span> implement:

    > RecommendationStrategy

### <span style="color:Aquamarine">ArtistStrategy, UserStrategy,

### <span style="color:Aquamarine">HostStrategy</span> implement:

    > AllUsersStrategy
