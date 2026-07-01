# raffletracker

a client side fabric mod for the century celebration event on hypixel skyblock. it puts a little
always visible overlay on your screen that tracks the raffle tasks you still need to do and counts
down to each raffle draw. its meant to look and feel like skyhanni but its totally standalone, you
dont need skyhanni installed and it doesnt hook into it at all.

## what it does

the overlay has a bold raffle tracker title and a see through background so it stays out of the way.
completed tasks get hidden, and each task you still need shows up with a short note on how to do it,
like scribe slayer, kill a scribe. they are grouped into the three tiers, easy from paper that
resets every 2 hours, medium from maps that resets every 24 hours, and hard from filled maps that
resets when the event ends.

it also shows a live countdown to each raffle draw, the speed one, the daily one, and the big one,
with the soonest one highlighted. all of the timing runs off real clock time so it stays right even
if your game lags or pauses.

the raffle tasks reset every time the speed raffle draws, so when that timer hits zero the tracker
clears the old task list and tells you to run the command again. it also watches chat, so the second
you see the message that you completed a raffle task, that task drops off the list on its own.

if you havent loaded any data yet the overlay just tells you which command to run.

## how to use it

run /centurytasks and open the raffle tasks chest, it reads it on its own.

run /centuryrafflebox and open the raffle box, it grabs the draw timers.

the overlay updates right away, and it rereads the chests every time you open them again.

## config

type /raffletracker or /rt to open the config screen.

hub has the on and off toggle for the tracker.

gui has edit position where you drag it around and scroll to change the size, a background toggle
and an opacity setting so you can make it solid if the see through is hard to read, a descriptions
toggle to hide the how to text and just show the task name, and a reset position button.

your settings save to config/raffletracker.json. the raffle data stays in memory for the whole
session so it carries over when you hop servers.

## building

you need jdk 25. point your java home at a jdk 25 and run the gradle build, something like
JAVA_HOME=/path/to/jdk25 ./gradlew build. the finished jar shows up in build/libs.

## license

cc0, do whatever you want with it.
