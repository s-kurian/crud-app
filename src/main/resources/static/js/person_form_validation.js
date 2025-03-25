function validateForm() {
  let isValid = true;

  document.querySelectorAll('.error-message').forEach(errorDiv => {
    errorDiv.textContent = '';
  });

  const firstNameField = document.getElementById('firstName');
  if (firstNameField.value.trim() === "") {
    document.getElementById('firstName-error').textContent = "First name is required.";
    isValid = false;
  } else if (firstNameField.value.trim().length > 50) {
    document.getElementById('firstName-error').textContent = "First name must be less than 50 characters.";
    isValid = false;
  }

  const lastNameField = document.getElementById('lastName');
  if (lastNameField.value.trim() === "") {
    document.getElementById('lastName-error').textContent = "Last name is required.";
    isValid = false;
  } else if (lastNameField.value.trim().length > 50) {
    document.getElementById('lastName-error').textContent = "Last name must be less than 50 characters.";
    isValid = false;
  }

  const emailAddressField = document.getElementById('emailAddress');
  if (emailAddressField.value.trim() === "") {
    document.getElementById('emailAddress-error').textContent = "Email address is required.";
    isValid = false;
  } else if (emailAddressField.value.trim().length > 50) {
    document.getElementById('emailAddress-error').textContent = "Email address must be less than 50 characters.";
    isValid = false;
  }

  const streetAddressField = document.getElementById('streetAddress');
  if (streetAddressField.value.trim() === "") {
    document.getElementById('streetAddress-error').textContent = "Street address is required.";
    isValid = false;
  } else if (streetAddressField.value.trim().length > 50) {
    document.getElementById('streetAddress-error').textContent = "Street address must be less than 50 characters.";
    isValid = false;
  }

  const cityField = document.getElementById('city');
  if (cityField.value.trim() === "") {
    document.getElementById('city-error').textContent = "City is required.";
    isValid = false;
  } else if (cityField.value.trim().length > 50) {
    document.getElementById('city-error').textContent = "City must be less than 50 characters.";
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
