// 1. AOS (Animate on Scroll) Initialization
AOS.init({
    duration: 800,
    once: true,
});

// 2. Image Preview Function
function previewImage(event) {
    const reader = new FileReader();
    reader.onload = function(){
        const output = document.getElementById('imagePreview');
        const previewText = document.getElementById('previewText');

        output.src = reader.result;
        output.style.display = 'block';

        if(previewText) {
            previewText.style.display = 'none';
        }
    };
    reader.readAsDataURL(event.target.files[0]);
}


// 3. Real-Time Chat Logic (UPDATED)
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const messageArea = document.querySelector('#messageArea');
const connectingElement = document.querySelector('.connecting');
const chatContainer = document.querySelector('#chatContainer'); // Get the main chat container

let stompClient = null;
let username = null;
let propertyId = null;
let isChatConnected = false; // <-- NEW: Flag to check if we are already connected

// NEW FUNCTION: This shows/hides the chat and connects on the first click
function toggleChat() {
    if (chatContainer) {
        // Toggle visibility
        const isHidden = chatContainer.style.display === 'none';
        chatContainer.style.display = isHidden ? 'block' : 'none';

        // Connect only if it's the first time opening the chat
        if (isHidden && !isChatConnected) {
            connect();
        }
    }
}

function connect() {
    username = document.querySelector('#username')?.innerText.trim();
    propertyId = document.querySelector('#propertyId')?.innerText.trim();

    if (username && propertyId) {
        isChatConnected = true; // Set flag to true
        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, onConnected, onError);
    }
}

// ... onConnected, onError, sendMessage, onMessageReceived functions are the same as before ...

function onConnected() {
    stompClient.subscribe('/topic/property/' + propertyId, onMessageReceived);
    stompClient.send("/app/chat.addUser/" + propertyId, {}, JSON.stringify({sender: username, type: 'JOIN', propertyId: propertyId}));
    if(connectingElement) connectingElement.style.display = 'none';
}

function onError(error) {
    if(connectingElement) {
        connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
        connectingElement.style.color = 'red';
    }
}

function sendMessage(event) {
    const messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        const chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT',
            propertyId: propertyId
        };
        stompClient.send("/app/chat.sendMessage/" + propertyId, {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

function onMessageReceived(payload) {
    const message = JSON.parse(payload.body);
    const messageElement = document.createElement('li');
    messageElement.classList.add('list-group-item');

    if(message.type === 'JOIN') {
        messageElement.classList.add('text-muted', 'text-center', 'fst-italic');
        message.content = message.sender + ' joined the chat!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('text-muted', 'text-center', 'fst-italic');
        message.content = message.sender + ' left the chat.';
    } else {
        const usernameElement = document.createElement('strong');
        usernameElement.classList.add('me-2');
        usernameElement.appendChild(document.createTextNode(message.sender + ':'));
        messageElement.appendChild(usernameElement);
    }

    const textElement = document.createElement('span');
    textElement.appendChild(document.createTextNode(message.content));
    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}


// Attach the submit event listener for the chat form
if (messageForm) {
    messageForm.addEventListener('submit', sendMessage, true);
}
// --- 5. Leaflet Map Initialization ---
function initMap() {
    const mapDiv = document.getElementById('map');
    if (!mapDiv) return; // Only run if the map div exists on the page

    const lat = mapDiv.dataset.lat;
    const lon = mapDiv.dataset.lon;

    if (lat && lon) {
        // Initialize the map and set its view to the property's coordinates
        const map = L.map('map').setView([lat, lon], 15); // 15 is the zoom level

        // Add the map background (tiles from OpenStreetMap)
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        }).addTo(map);

        // Add a marker (pin) to the map at the property's location
        L.marker([lat, lon]).addTo(map)
            .bindPopup('Property Location')
            .openPopup();
    }
}

// Call the function to initialize the map if it's present on the page
initMap();