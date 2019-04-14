window.addEventListener("load", function() {

    let tiles = document.getElementsByClassName("map-tile");
    for(let i = 0; i < tiles.length; i++) {
        let tile = tiles[i];
        tile.addEventListener("mouseover", function() {
            console.log(tile.getAttribute("region"));
        });
    }

});