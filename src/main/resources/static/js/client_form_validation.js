
function validateForm() {
  let isValid = true;

  document.querySelectorAll('.error-message').forEach(errorDiv => {
    errorDiv.textContent = '';
  });

  const nameField = document.getElementById('name');
  if (nameField.value.trim() === "") {
    document.getElementById('name-error').textContent = "Client name is required.";
    isValid = false;
  }

  const websiteUrlField = document.getElementById('websiteUrl');
  if (websiteUrlField.value.trim() === "") {
    document.getElementById('websiteUrl-error').textContent = "Website URL is required.";
    isValid = false;
  }

  const phoneNumberField = document.getElementById('phoneNumber');
  const phonePattern = /^\d{10}$/; 
  if (!phonePattern.test(phoneNumberField.value)) {
    document.getElementById('phoneNumber-error').textContent = "Phone number must be 10 digits.";
    isValid = false;
  }

  const streetAddressField = document.getElementById('streetAddress');
  if (streetAddressField.value.trim() === "") {
    document.getElementById('streetAddress-error').textContent = "Street address is required.";
    isValid = false;
  }

  const cityField = document.getElementById('city');
  if (cityField.value.trim() === "") {
    document.getElementById('city-error').textContent = "City is required.";
    isValid = false;
  }

  const stateField = document.getElementById('state');
  if (stateField.value.trim().length !== 2) {
    document.getElementById('state-error').textContent = "State must be 2 characters.";
    isValid = false;
  }

  const zipCodeField = document.getElementById('zipCode');
  if (zipCodeField.value.trim().length !== 5 || isNaN(zipCodeField.value)) {
    document.getElementById('zipCode-error').textContent = "Zip code must be 5 digits.";
    isValid = false;
  }

  return isValid;
}

document.getElementById('clientForm').addEventListener('submit', function(event) {
  event.preventDefault();
  if (validateForm()) {
    this.submit(); 
  }
});
