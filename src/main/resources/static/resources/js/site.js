var moneyConversions = document.querySelectorAll(".moneyConversion");
for(let element of moneyConversions){
	let convertedValue = Number(element.value).toFixed(2);
	element.value = convertedValue;
}
var numberInputs = document.querySelectorAll("[type=number]");
for(let element of numberInputs){
	if(element.value == ""){
		element.value = 0;
	}
}
var dateInputs = document.querySelectorAll("[type=date]");
for(let element of dateInputs){
	if(element.getAttribute("value") != ""){
		let dateValue = new Date(element.getAttribute("value"));
		let formatted = dateValue.toISOString().split('T')[0];
		element.value = formatted;
	}
}

let dropdown = document

let alternateAction = document.querySelector(".alternate-action table");
if(alternateAction != null){
	let actionType = alternateAction.getAttribute("data-action");
	document.querySelector(".alternate-action").setAttribute("action", actionType);
	if(actionType === "update"){
		document.querySelector(".readonly-id").setAttribute("readonly", "readonly");
	}
}
let role = document.querySelector(".role");
if(role != null){
	let roleName = role.textContent;
	let formatted = roleName.replace('[', '').replace(']', '').replace('ROLE_', '');
	role.textContent = formatted;
}
let mainBody = document.querySelector(".main-body");
let roleName = mainBody.getAttribute("data-role");
let formatted = roleName.replace('[', '').replace(']', '').replace('ROLE_', '');
mainBody.setAttribute("data-role", formatted);

let date_1 = 0;
let date_2 = 0;
function DateIni1(data){
date_1 = data.value;
ChangePrice();
}
function DateIni2(data){
date_2 = data.value;
ChangePrice();
}

function ChangePrice(){
if(date_1 == 0 || date_2 == 0){
    document.getElementById("bill").value = 0;
} else{
    let oneDay = 24 * 60 * 60 * 1000; // hours*minutes*seconds*milliseconds
    let firstDate = new Date(date_2);
    let secondDate = new Date(date_1);
    let diffDays = Math.round(Math.abs((firstDate - secondDate) / oneDay));
    let priceDays = document.getElementById("price").value;
    let totalPrice = (diffDays + 1) * priceDays;
    document.getElementById("bill").value = totalPrice;
}

}