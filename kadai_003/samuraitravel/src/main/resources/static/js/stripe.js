const stripe = Stripe('pk_test_51PAvemF7SBj4Oshj6k55jeFREZseZ7ttejoraxzIPy4GphGkricNaqvHoUPUizsZk1EfmaNdCg4VT6aYg0sayZUV00CIvPcmqk');
const paymentButton = document.querySelector('#paymentButton');
 
 paymentButton.addEventListener('click', () => {
   stripe.redirectToCheckout({
     sessionId: sessionId
   })
 });