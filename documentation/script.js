document.onload = (event) => {
	let examples = document.getElementsByClassName("example");

	for (let index = 0; index < examples.length; index++) {
		const element = examples[index];
		element.style.display = "none";
	}
};

function makeVisible(el) {
	document.getElementById(el).style.display = "block";
}
