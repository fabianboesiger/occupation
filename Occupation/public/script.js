window.addEventListener("load", function() {

    // Replace Date
	let year = new Date().getFullYear();
	let years = document.getElementsByClassName("year");
	for(let i = 0; i < years.length; i++) {
		years[i].innerHTML = year;
	}

	// Set Navigation
	let buttons = document.getElementsByTagName("nav")[0].getElementsByTagName("a");
	for(let i = 0; i < buttons.length; i++) {
		let button = buttons[i];
		if(button.href === window.location.href) {
			button.classList.add("active");
		}
    }
    
    // HTML Loader
    let loaders = document.getElementsByClassName("loader");
    for(let i = 0; i < loaders.length; i++) {
        loadHTML(loaders[i].getAttribute("href"), loaders[i]);
        setInterval(function() {
            loadHTML(loaders[i].getAttribute("href"), loaders[i]);
        }, loaders[i].getAttribute("interval"));
    }

    updatePage(document);
});

// Loader
function loadHTML(href, element) {
	let request = new XMLHttpRequest();
	request.open("GET", href, true);
	request.send(null);
	request.onload = function(e) {
		if(request.readyState === 4) {
			if(request.status === 200) {
                element.innerHTML = request.responseText;
                updatePage(element);
			}
		}
	}
}

// Copy to Clipboard
function copy(element, text) {
	let temporary = element.getAttribute("switch");
	element.setAttribute("switch", element.innerHTML);
	element.innerHTML = temporary;
	if(element.classList.contains("active")) {
		element.classList.remove("active");
	} else {
		let pseudo = document.createElement("input");
		pseudo.value = text;
		document.body.appendChild(pseudo);
		pseudo.select();
		document.execCommand("copy");
		pseudo.parentElement.removeChild(pseudo);
		element.classList.add("active");
	}
}

function updatePage(target) {
    // Animate Images
    animate(target);
    // Automate Forms
    automateForms(target);
    // Healthbars
    buildHealthbars(target);
}

// Image Animator
function animate(target) {
    let frame = [];
    let animations = target.getElementsByClassName("animation");
    for(let i = 0; i < animations.length; i++) {
        frame[i] = 0;
        animations[i].setAttribute("src", animations[i].getAttribute("path") + "/" + frame[i] + ".png");
        setInterval(function() {
            frame[i]++;
            if(frame[i] >= animations[i].getAttribute("frames")) {
                frame[i] = 0;
            }
            animations[i].setAttribute("src", animations[i].getAttribute("path") + "/" + frame[i] + ".png");
        }, 500);
    }
}

// Automated Form Submit
function automateForms(target) {
    let automatedForms = target.getElementsByClassName("automated");
    for(let i = 0; i < automatedForms.length; i++) {
        let automatedInputs = automatedForms[i].getElementsByTagName("input");
        for(let j = 0; j < automatedInputs.length; j++) {
            automatedInputs[j].addEventListener("change", function() {
                // Get Form Parameters
                let pairs = [];
                for(let k = 0; k < this.form.elements.length; k++) {
                    let element = this.form.elements[k];
                    if(element.type === "radio") {
                        if(element.checked) {
                            pairs.push(encodeURIComponent(element.name) + "=" + encodeURIComponent(element.value));
                        }
                    } else {
                        pairs.push(encodeURIComponent(element.name) + "=" + encodeURIComponent(element.value));
                    }
                }
                let parameters = pairs.join("&");

                // Send Form Parameters
                let request = new XMLHttpRequest();
                request.open("POST", this.form.getAttribute("action"), true);
                request.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
                request.send(parameters);
            });
        }
    }
}

// Build Healthbars
function buildHealthbars(target) {
    let healthbars = target.getElementsByClassName("healthbar");
    for(let i = 0; i < healthbars.length; i++) {
        let value = parseFloat(healthbars[i].getAttribute("value"));
        
        let bar = document.createElement("div");
        bar.classList.add("bar");
        bar.style.width =  (healthbars[i].clientWidth / 100 * value) + "px";

        let percentage = document.createElement("div");
        percentage.classList.add("percentage");
        percentage.innerHTML = Math.ceil(value) + "%";

        let icon = document.createElement("img");
        icon.setAttribute("src", healthbars[i].getAttribute("icon"));
        icon.classList.add("icon");
        
        healthbars[i].appendChild(bar);
        healthbars[i].appendChild(icon);
        healthbars[i].appendChild(percentage);
    }
}