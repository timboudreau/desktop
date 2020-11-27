Swing Miscellaneous
===================

Miscellaneous Swing components and utilities that have proven useful in more
than one application.

Specifically:

 * Self-sizing empty borders that use the component's font and font metrics to determine
margins
 * Activity indicator - unobtrusive indicator that _something_ happened in the background, where
a cycling JSlider would be too intrusive
 * `EnumComboBoxModel` - generate combo boxes from a collection of enums quickly and easily
 * `TextCell` - an alternative to using HTML in cell renderers or elsewhere - cells with
different text attributes (font, colors, background shape, font size) which can be composed
together to render text; `TextCellCellRenderer` implements a high-performance tree/list cell
renderer using it - `TextCell` is not a component per-se, just a thing that can render text
and its background.
 * `Spinner` - an exotic looking spinnning "loading" icon-like thing
 * `Cursors` - a large set of arrow, polygon and other abstruse cursors useful for image
editors, possibly other things
 * `TextWrapLabelUI` - for multiline JLabels without using HTML
 * Several layout managers:
    * `VerticalFlowLayout` - does exactly what its name says
    * `TilingLayout` - tiles square(ish) components optimally within the container's bounds,
with a few settable policies for how to make decisions
 * Several replacement UIs for JSlider
    * `PopupSliderUI` displays a number, and the slider pops up when the mouse is pressed,
updates based on relative motion of the mouse from the start point, and updates the value on
mouse released
    * `RadialSliderUI` a slider UI that works like moving a hand on an analog clock
