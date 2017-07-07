# SpellMadness

SpellMadness is a game designed and developed by me when I was starting to dive into game development.
It was never finished, but it has a playable version.

It was programmed using [JAVA](https://www.java.com/) with libraries like: [LWJGL](https://www.lwjgl.org/), [Slick2D](http://slick.ninjacave.com/), and others.

## Quick links

I didn't know that time, but I was starting to write code using some patterns, and I find this very interesting.

I've used [Scenes](https://github.com/AndreMicheletti/SpellMadness_game/blob/master/engine/scene/SceneBase.java) to define each play mode and put the managing of it inside the [main loop](https://github.com/AndreMicheletti/SpellMadness_game/blob/master/engine/Engine.java#L118) for the game.

[Character](https://github.com/AndreMicheletti/SpellMadness_game/blob/master/engine/object/Character.java) class to control the [Physics2D](https://github.com/AndreMicheletti/SpellMadness_game/blob/master/engine/object/Character.java#L105) of the characters in the world.

For the Visual Effetcs, there is a [Particle](https://github.com/AndreMicheletti/SpellMadness_game/blob/master/engine/effects/Particle.java) class that is the tiny visual elements that are spawned by [AnimationFX](https://github.com/AndreMicheletti/SpellMadness_game/blob/master/engine/effects/AnimationFX.java) class to create the Spell's effects.

And the [LightFX](https://github.com/AndreMicheletti/SpellMadness_game/blob/master/engine/effects/LightFX.java) create the lightning of the scene by drawing a bitmap with AlphaBlending and putting some life to it.

## About the Game

It is a 2D platformer where you control a mage that can cast several spells. It has nice lightning and effects.

Watch a demonstration video:

[![Watch a demo](https://j.gifs.com/8qRRgr.gif)](https://www.youtube.com/watch?v=QzRUxwdqFXE)

### Download

You can download the .jar [playable version](https://mega.nz/#!Q5Y33BYA!ce7ezLAvRj-F3pIYMFq0TbFoilzFbhkZ3NS8r3m8kkM)

## Credits

- Brian Matzon <brian@matzon.dk> for [SoundManager](https://github.com/AndreMicheletti/SpellMadness_game/blob/master/main/SoundManager.java)
- [Slick2D](http://slick.ninjacave.com/)
- [LWJGL](https://www.lwjgl.org/)
