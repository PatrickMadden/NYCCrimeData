$(document).ready(function () {
	const menu = $('.header-menu');
	const burger = $('.svn-burger');

	$('.menu-burger').on('click', function () {
		// Open or close the menu.
		menu.toggle(0);

		// Switch highlight color of the burger from dark grey to active blue.
		burger.toggleClass('svg-icon');
	});

	menu.on('click', function (event) {
		// Close the menu when a child element inside the menu is clicked.
		if (event.target !== this)
		{
			menu.hide();
			burger.addClass('svg-icon');
		}
	});

	menu.on('mouseleave', function () {
		// Close the menu when the mouse cursor leaves its area.
		menu.hide();
		burger.addClass('svg-icon');
	});
});