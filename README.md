# NordMicroModular
A set of patches optimized for the Micro Modular

This project is intended to include a set of Nord Modular patches optimized for the Micro Modular. The goal is to help make the Micro Modular into a usable performance synthesizer, despite its limited DSP power and small control surface. The patches will conform to a set of coding standards designed to help them be musically useful in this limited environment. The standards include:

 - Strict attention to DSP utilization, to acheive a determined level of polyphony. For instance, many patches will aim for 4- or 5- voice polyphony, limiting them to 20% or 25% CPU per voice.
 - No effects. External reverb and chorus effects are easy to find, and are considered to be cheaper than Micro Modular DSP cycles.
 - Attribution. Many patches will be derived from patches posted on the internet 20 or more years ago. The original patch names are retained, and the "Notes" area of the patch is used to credit the original source.
 - Three knobs mapped. All three knobs on the Micro Modular are mapped to be as useful as possible to a patch. Often this means that a knob will control a morph group.
 - Modulation wheel and aftertouch mapped.  
 - General MIDI CC mappings. All relevant patch parameters are mapped to a set of CCs that make them automatically accessible from a variety of controllers. Specific CCs are listed below.
 - One output channel. True stereo patches are allowed, and it is okay to patch output 1 to output 2 for the benefit of headphones. But in tight CPU situations, the .1% per voice saved by using a mono output can be significant. Users should expect patches to have just a single output.
 - Automated tooling. Nord Modular "G1" patch files are stored in a simple plain text format. This project aims to develop tools that can be used to maniplulate patch files in order apply these standards.





