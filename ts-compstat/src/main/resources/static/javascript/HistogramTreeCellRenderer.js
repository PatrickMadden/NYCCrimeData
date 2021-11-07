console.log("Loading custom HistogramTreeCellRenderer script");

/* Constructor -- called in container gwt widget's constructor. */
function HistogramTreeCellRenderer(params, parent) {
	this.parent = parent;
	this.labelElement = params.data.label;

	if (typeof params.data.nodeValue === "string"
		&& /^\d+:\d+:\d+$/.test(params.data.nodeValue))
	{
		var array = params.data.nodeValue.split(":");

		this.labelWidth = Number(array[0]);
		this.value = Number(array[1]);
		this.valueMax = Number(array[2]);
	}
	else
	{
		this.labelWidth = 80;
		this.value = 0;
		this.valueMax = 100;
	}

	if (this.valueMax == 0)
	{
		this.displayWidth = 0;
	}
	else
	{
		this.displayWidth = Math.round((this.value / this.valueMax)
			* HistogramTreeCellRenderer.MAX_BAR_WIDTH);
	}

	this.init();
};

HistogramTreeCellRenderer.MAX_BAR_WIDTH = 100;

HistogramTreeCellRenderer.HEIGHT = 22;


/* Not a method called by gwt code. For implementation purposes. */
HistogramTreeCellRenderer.prototype.init = function() {
	var histogram = document.createElement("table");
	histogram.style.verticalAlign = "middle";

	var tbody = document.createElement("tbody");
	histogram.appendChild(tbody);

	var row = document.createElement("tr");
	tbody.appendChild(row);

	var barContainer = document.createElement("td");
	barContainer.style.height = HistogramTreeCellRenderer.HEIGHT + "px";
	barContainer.style.width = this.displayWidth + "px";
	row.appendChild(barContainer);

	var bar = document.createElement("div");
	bar.style.width = this.displayWidth + "px";
	bar.style.height = "52%";
	bar.style.backgroundColor = "#0096FF";
	bar.style.border = "1px solid black";
	barContainer.appendChild(bar);

	var valueLabel = document.createElement("td");
	valueLabel.appendChild(document.createTextNode(Math.round(this.value)));
	valueLabel.style.height = HistogramTreeCellRenderer.HEIGHT + "px";
	valueLabel.style.paddingLeft = "3px";
	valueLabel.style.paddingRight = "10px";
	valueLabel.style.font = "11px sans-serif";
	valueLabel.style.color = "black";
	row.appendChild(valueLabel);

	this.histogram = histogram;
};


/*
 * Called by container gwt widget's onLoad.
 * This function should append the ui to the parent node.
 */
HistogramTreeCellRenderer.prototype.attach = function() {
	this.parent.appendChild(this.histogram);

	/* Set the width of the tree node label. */
	this.labelElement.style.width = this.labelWidth + "px";
};


/*
 * Called by container gwt widget's onUnload. Break the reference from the
 * container to allow garbage collection.
 */
HistogramTreeCellRenderer.prototype.destroy = function() {
	try {
		this.parent.removeChild(this.histogram);
	}
	catch (e) {}
};
