# BITCOINMAP.CASH / BMAP.CASH - Android App

This is the repo of the Android App "BITCOINMAP.CASH"

https://play.google.com/store/apps/details?id=club.therealbitcoin.bchmap

Feel free to participate! Check the issues to have something to get into the app!

The places are being hosted on:
https://realbitcoinclub.firebaseapp.com/places8.json

If you want to add more places, just make a pull request on the other repository:
https://github.com/theRealBitcoinClub/realbitcoinclub-website/blob/master/places8.json

And you would have to add a .gif and .webp in the dimensions 640x480 here with (p:) being the filename:
https://github.com/theRealBitcoinClub/realbitcoinclub-website/tree/master/img/app

You can use the https://ezgif.com/webp-maker and the settings are explained in the following pictures:
We use fading for the WEBP images and we use the exact same settings which create a fading effect while keeping the file size as small as possible.
(https://github.com/theRealBitcoinClub/BITCOINMAP.CASH---Browser-PWA/blob/master/public/images/webpmakersettings.JPG)
Do not use fading for the GIFs
(https://github.com/theRealBitcoinClub/BITCOINMAP.CASH---Browser-PWA/blob/master/public/images/gifmakersettings.JPG)
Always compress the GIF with a lossy algorithm of 30%
(https://bitcoinmap.cash/images/gifmakersettings2.JPG)
(https://github.com/theRealBitcoinClub/BITCOINMAP.CASH---Browser-PWA/blob/master/public/images/gifmakersettings3.JPG)

Each WEBP/GIF has 3 images of the shop and one promo image as the last frame:
(https://github.com/theRealBitcoinClub/BITCOINMAP.CASH---Browser-PWA/blob/master/public/bitcoincashmaplogo640x480.jpg)

The JSON file can be read like this:
```
[{"p(ID)":"trbc", "x":"41.406595", "y":"2.16655","n(NAME)":"TRBC - The Real Bitcoin Club", "t(TYPE/ICON)":"99","c(REVIEW COUNT)":"3","s(REVIEW STARS)":"5.0", "d(DISCOUNT TYPE)":"3", "a(TAGS)":"0,1,2,34", "l(LOCATION)":"Barcelona, Spain, Europe"}

So for this place there exist images here:
https://github.com/theRealBitcoinClub/realbitcoinclub-website/tree/master/img/app/trbc.webp
https://github.com/theRealBitcoinClub/realbitcoinclub-website/tree/master/img/app/trbc.gif
```

See below to find a reference for the tags/attributes (a:) and discount texts (d:) which are stored as numbers in the json file.

ACRA Crash reports are hosted on tracepot.

Test coverage is low as I was working alone on that app! This should be improved!

All Pull requests are welcome! I will be travelling for a long time, I hope someone takes over this project!

```
DISCOUNT TEXT
 <string name="discount0">10% discount on first Bitcoin payment</string>
    <string name="discount1">20% discount on first Bitcoin payment</string>
    <string name="discount2">Accepting Bitcoin payments soon</string>
    <string name="discount3">Trade Bitcoin here with 0.0% fee</string>
    <string name="discount4">DASH, BCH, BTC accepted here</string>
    <string name="discount5">Information from discoverdash.com</string>
```


````
TAGS/ATTRIBUTES

<item>Bitcoin</item>
        <item>Events</item>
        <item>Trading</item>
        <item>Organic</item>
        <!--4--><item>Veggie</item>
        <item>Vegan</item>
        <item>Healthy</item>
        <item>Burger</item>
        <item>Sandwich</item>
        <!--9--><item>Muffin</item>
        <item>Brownie</item>
        <item>Cake</item>
        <item>Cookie</item>
        <item>Tiramisu</item>
        <!--14--><item>Pizza</item>
        <item>Salad</item>
        <item>Smoothie</item>
        <item>Fruit</item>
        <item>Gelato</item>
        <!--19--><item>Raw</item>
        <item>Handbag</item>
        <item>Cosmetic</item>
        <item>Tattoo</item>
        <item>Piercing</item>
        <!--24--><item>Souvenir</item>
        <item>Hatha</item>
        <item>Vinyasa</item>
        <item>Massage</item>
        <item>Upcycled</item>
        <!--29--><item>Coffee</item>
        <item>NoGluten</item>
        <item>Cocktail</item>
        <item>Beer</item>
        <item>Music</item>
        <!--34--><item>Projects</item>
        <item>Electro</item>
        <item>Rock</item>
        <item>LiveDJ</item>
        <item>Terrace</item>
        <!--39--><item>Seeds</item>
        <item>Grinder</item>
        <item>Papers</item>
        <item>Advice</item>
        <item>Calzone</item>
        <!--44--><item>Suppli</item>
        <item>MakeUp</item>
        <item>Gifts</item>
        <item>Tapas</item>
        <item>Copas</item>
        <!--49--><item>Piadina</item>
        <item>Hierba</item>
        <item>Cereales</item>
        <item>Fashion</item>
        <item>Fair</item>
        <!--54--><item>Women</item>
        <item>Drinks</item>
        <item>TV</item>
        <item>Retro</item>
        <item>Color</item>
        <!--59--><item>B/W</item>
        <item>BTC</item>
        <item>BCH</item>
        <item>Online</item>
        <item>Booking</item>
        <!--64--><item>HotDog</item>
        <item>Fast</item>
        <item>Kosher</item>
	<item>Sushi</item>
	<item>Moto</item>
	<!--69--><item>Coche</item>
	<item>Tablet</item>
	<item>Chicken</item>
	<item>Rabbit</item>
	<item>Potatoe</item>
	<!--74--><item>DASH</item>
	<item>ETH</item>
	<item>ATM</item>
	<item>Club</item>
	<item>Dance</item>
	<!--79--><item>TakeAway</item>
	<item>Meditation</item>
	<item>Wine</item>
	<item>Champagne</item>
	<item>Alcohol</item>
	<!--84--><item>Booze</item>
	<item>Hookers</item>
	<item>Girls</item>
	<item>Gay</item>
	<item>Party</item>
	<!--89--><item>English</item>
	<item>B&amp;B</item>
	<item>Haircut</item>
	<item>Nails</item>
	<item>Beauty</item>
	<!--94--><item>Miso</item>
	<item>Teriyaki</item>
	<item>Rice</item>
	<item>Seafood</item>
	<item>Hostel</item>
	<!--99--><item>Fries</item>
	<item>Fish</item>
	<item>Chips</item>
	<item>Italian</item>
	<item>Karaoke</item>
	<!--104--><item> x x x </item>
	<item>Battery</item>
	<item>Wheels</item>
	<item>Men</item>
  <item>Pasta</item>
	<!--109--><item>Dessert</item>
  <item>Starter</item>
  <item>BBQ</item>
  <item>Noodle</item>
  <item>Korean</item>
	<!--114--><item>Market</item>
  <item>Bread</item>
  <item>Bakery</item>
  <item>Cafe</item>
  <item>Games</item>
	<!--119--><item>Snacks</item>
  <item>Elegant</item>
  <item>Piano</item>
  <item>Brunch</item>
  <item>Nachos</item>
	<!--124--><item>Lunch</item>
  <item>Breakfast</item>
  <item>HappyHour</item>
  <item>LateNight</item>
  <item>Mexican</item>
	<!--129--><item>Burrito</item>
  <item>Tortilla</item>
  <item>Indonesian</item>
  <item>Sports</item>
  <item>Pastry</item>
	<!--134--><item>Bistro</item>
  <item>Soup</item>
  <item>Tea</item>
  <item>Onion</item>
  <item>Steak</item>
	<!--139--><item>Shakes</item>
  <item>Empanadas</item>
  <item>Dinner</item>
  <item>Sweet</item>
  <item>Fried</item>
	<!--144--><item>Omelette</item>
  <item>Gin</item>
  <item>Donut</item>
  <item>Delivery</item>
  <item>Cups</item>
	<!--149--><item>Filter</item>
  <item>Juice</item>
  <item>Vietnamese</item>
  <item>Pie</item>
  <item>Unagi</item>
	<!--154--><item>Greek</item>
  <item>Japanese</item>
  <item>Tacos</item>
  <item>Kombucha</item>
  <item>Indian</item>
	<!--159--><item>Nan</item>
  <item>Club</item>
  <item>Liquor</item>
  <item>Pool</item>
  <item>Hotel</item>
	<!--164--><item>Pork</item>
  <item>Ribs</item>
  <item>Kava</item>
  <item>Chai</item>
  <item>Izzy</item>
	<!--164--><item>Matcha</item>
  <item>CBD</item>
  <item>Latte</item>
 ```
