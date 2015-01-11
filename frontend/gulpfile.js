var gulp = require('gulp'),
    gutil = require('gulp-util'),
    uglify = require('gulp-uglify'),
    concat = require('gulp-concat'),
    notify = require('gulp-notify'),
    sourcemaps = require('gulp-sourcemaps'),
    less = require('gulp-less'),
    minifyCSS = require('gulp-minify-css'),
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
    return gulp.src('src/styles/client.less')
        .pipe(less())
        //.pipe(minifyCSS())
        .pipe(gulp.dest('../public/stylesheets'));
});

gulp.task('frontend', ['scripts', 'styles']);

gulp.task('watch', function() {
    gulp.watch('src/scripts/**/*', ['scripts']);
    gulp.watch('src/styles/**/*', ['styles']);
});