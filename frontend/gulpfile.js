var gulp = require('gulp'),
    gutil = require('gulp-util'),
    uglify = require('gulp-uglify'),
    concat = require('gulp-concat'),
    notify = require('gulp-notify'),
    sourcemaps = require('gulp-sourcemaps'),
    less = require('gulp-less'),
    minifyCSS = require('gulp-minify-css'),
    jade = require('gulp-jade'),
    del = require('del');

gulp.task('scripts', function() {
    //return gulp.src(['src/scripts/app/**/index.js', 'src/scripts/bootstrap.js','src/scripts/app/**/*.js'])
    return gulp.src(['src/scripts/app/**/*.js', 'src/scripts/bootstrap.js'])
        .pipe(sourcemaps.init())
        .pipe(concat('client.js'))
        //.pipe(sourcemaps.write())
        .pipe(gulp.dest('../public/javascripts'))
});

gulp.task('styles', function() {
    gulp.src('src/styles/*.less')
        .pipe(less())
        //.pipe(minifyCSS())
        .pipe(gulp.dest('../public/stylesheets'));
});

gulp.task('templates', function() {
    var YOUR_LOCALS = {};

    gulp.src('src/templates/*.jade')
        .pipe(jade({
            locals: YOUR_LOCALS
        }))
        .pipe(gulp.dest('../public/templates/'));
    gulp.src('src/templates/**/*.jade')
        .pipe(jade({
            locals: YOUR_LOCALS
        }))
        .pipe(gulp.dest('../public/templates/'));
});

gulp.task('frontend', ['scripts', 'styles', 'templates']);
//gulp.task('frontend', ['scripts', 'templates']);

gulp.task('watch', function() {
    gulp.watch('src/scripts/**', ['scripts']);
    gulp.watch('src/styles/**', ['styles']);
    gulp.watch('src/templates/**', ['templates']);
});